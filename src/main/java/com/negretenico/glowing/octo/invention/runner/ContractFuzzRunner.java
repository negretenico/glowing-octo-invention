package com.negretenico.glowing.octo.invention.runner;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.contracts.Contract;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.invariants.Invariant;
import com.negretenico.glowing.octo.invention.service.ContractFuzzerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Supplier;

@Configuration
public class ContractFuzzRunner {
    private <T,U extends Contract<T>> void run(ContractFuzzerService<T,U> cfs,
                                               Supplier<T> createRandom,
                                               int limit,
                                               Invariant<U> invariant){
        System.out.println("Starting fuzzing");
        for(int i =0; i< limit; i ++){
            T rnd = createRandom.get();
            System.out.println("Fuzzing for "+rnd);
            cfs.fuzz(rnd);
            invariant.check(cfs.getContract());
        }
        System.out.println("Finished fuzzing");
    }
    @Bean
    @Profile("bank")
    public CommandLineRunner bank(ContractFuzzerService<BigInteger,
            BankContract> contractFuzzerService,
                                  Invariant<BankContract> invariant){
        return args ->{
            Random rnd = new Random();
            Supplier<BigInteger> create =
                    ()->BigInteger.valueOf(rnd.nextInt(100));
            run(contractFuzzerService,create,10,invariant);
        };
    }
    @Bean
    @Profile("insurance")
    public CommandLineRunner insurance(ContractFuzzerService<BigInteger,
            InsuranceContract> insuranceContract,
                                       Invariant<InsuranceContract> invariant){
        return arg ->{
            Random rnd = new Random();
            Supplier<BigInteger> create =
                    ()->{
                        BigInteger val =  BigInteger.valueOf(rnd.nextInt(500));
                        if(val.compareTo(BigInteger.valueOf(50))>0){
                            insuranceContract.updateContract(insuranceContract.getContract().claimEvent());
                        }
                        return val;
                    };
            run(insuranceContract,create,36,invariant);
            Result<BigInteger> payout =
                    insuranceContract.getContract().payout();
            String message =
                    payout.isSuccess()?String.format("We will payout %s",
                            payout.data()) :
                            payout.errorMsg();
            System.out.printf(message);
        };
    }
}