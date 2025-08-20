package com.negretenico.glowing.octo.invention.runner;

import com.negretenico.glowing.octo.invention.models.Report;
import com.negretenico.glowing.octo.invention.models.contracts.Bank;
import com.negretenico.glowing.octo.invention.models.contracts.Web3jBankContract;
import com.negretenico.glowing.octo.invention.service.FileWriterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.IntStream;

@Configuration
public class Web3ContractRunner {
	@Bean
	@Profile("web3jbank")
	public CommandLineRunner runner(Bank bank,
																	FileWriterService fileWriterService,
																	Credentials web3JCredentials,
																	Web3j web3j) {
		return args -> {

			Random rnd = new Random();
			int limit = 100;
			Report report = new Report()
					.contractType("Web3jBankContract")
					.iterations(limit);
			Web3jBankContract bankContract = new Web3jBankContract(bank, Convert.Unit.ETHER, report);
			IntStream.range(0, limit)
					.forEach(i -> {
						System.out.printf("We are on iteration %s out of %s\n", i, limit);
						int discrete = rnd.nextInt(100);
						bankContract.fuzz(discrete, BigInteger.valueOf(rnd.nextInt(500)),
								web3j, web3JCredentials);
					});
			report.finalState("DONE");
			fileWriterService.writeReport(report);
		};
	}
}
