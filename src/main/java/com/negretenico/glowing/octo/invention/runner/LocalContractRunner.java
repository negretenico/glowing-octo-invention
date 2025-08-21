package com.negretenico.glowing.octo.invention.runner;

import com.common.functionico.evaluation.Result;
import com.common.functionico.risky.Try;
import com.negretenico.glowing.octo.invention.models.Report;
import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.contracts.Contract;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.oracle.Oracle;
import com.negretenico.glowing.octo.invention.service.ContractFuzzerService;
import com.negretenico.glowing.octo.invention.service.FileWriterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Configuration
public class LocalContractRunner {
	private final FileWriterService fileWriterService;

	public LocalContractRunner(FileWriterService fileWriterService) {
		this.fileWriterService = fileWriterService;
	}

	private <T, U extends Contract<T>> Report run(ContractFuzzerService<T, U> cfs,
																								Supplier<T> createRandom,
																								int limit,
																								Oracle<U> oracle) {
		Report report = new Report()
				.contractType(cfs.getContract().getClass().getSimpleName())
				.iterations(limit);
		IntStream.range(0, limit)
				.forEach(i -> {
					T rnd = createRandom.get();
					cfs.fuzz(rnd);
					Try.of(() -> oracle.check(cfs.getContract()))
							.onSuccess(s -> report.addOracleCheck("Iteration " + i +
									": ✅ Passed"))
							.onFailure(e -> report.addOracleCheck(
									"Iteration " + i + ": ❌ Failed - " + e.getMessage()));

				});
		report.finalState(cfs.getContract().toString());
		System.out.println("Finished fuzzing");
		return report;
	}

	@Bean
	@Profile("bank")
	public CommandLineRunner bank(ContractFuzzerService<BigInteger,
																		BankContract> contractFuzzerService,
																Oracle<BankContract> oracle) {
		return args -> {
			Random rnd = new Random();
			Supplier<BigInteger> create =
					() -> BigInteger.valueOf(rnd.nextInt(100));
			fileWriterService.writeReport(run(contractFuzzerService, create, 10
					, oracle));
		};
	}

	@Bean
	@Profile("insurance")
	public CommandLineRunner insurance(ContractFuzzerService<BigInteger,
																				 InsuranceContract> insuranceContract,
																		 Oracle<InsuranceContract> oracle) {
		return arg -> {
			Random rnd = new Random();
			Supplier<BigInteger> create =
					() -> {
						BigInteger val = BigInteger.valueOf(rnd.nextInt(500));
						if (val.compareTo(BigInteger.valueOf(50)) > 0) {
							insuranceContract.updateContract(insuranceContract.getContract().claimEvent());
						}
						return val;
					};
			fileWriterService.writeReport(run(insuranceContract, create, 36, oracle));
			Result<BigInteger> payout =
					insuranceContract.getContract().payout();
			String message =
					payout.isSuccess() ? String.format("We will payout %s",
							payout.data()) :
							payout.errorMsg();
			System.out.printf(message);
		};
	}
}