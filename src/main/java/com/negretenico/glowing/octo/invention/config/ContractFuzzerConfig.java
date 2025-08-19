package com.negretenico.glowing.octo.invention.config;

import com.negretenico.glowing.octo.invention.models.Web3jProperties;
import com.negretenico.glowing.octo.invention.models.contracts.Bank;
import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.contracts.Web3jBankContract;
import com.negretenico.glowing.octo.invention.service.ContractFuzzerService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.utils.Convert;

import java.math.BigInteger;

@Configuration
@EnableConfigurationProperties(Web3jProperties.class)
public class ContractFuzzerConfig {
    @Bean
    @Profile("bank")
    public ContractFuzzerService<BigInteger,BankContract> bankContractFuzzService(){
        return new ContractFuzzerService<>(new BankContract(BigInteger.ZERO));
    }
    @Bean
    @Profile("insurance")
    public ContractFuzzerService<BigInteger, InsuranceContract> insuranceContractFuzzService(){
        BigInteger premiumAmountPaid = BigInteger.ZERO;
        BigInteger coverage = BigInteger.valueOf(200_000);
        BigInteger minimumPremiumAmount = BigInteger.valueOf(2_000);
        return new ContractFuzzerService<>(new InsuranceContract(premiumAmountPaid,coverage,false,minimumPremiumAmount));
    }
    @Bean
    @Profile("web3jbank")
    public ContractFuzzerService<BigInteger, Web3jBankContract> web3jBankContractFuzzerService(Bank bank) {
        Web3jBankContract web3jBankContract = new Web3jBankContract(bank,
                Convert.Unit.ETHER);
        return new ContractFuzzerService<>(web3jBankContract);
    }
}
