package com.negretenico.glowing.octo.invention.config;

import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.invariants.EarlyPayout;
import com.negretenico.glowing.octo.invention.models.invariants.Invariant;
import com.negretenico.glowing.octo.invention.models.invariants.NonNegativeBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class InvariantConfig {
    @Bean
    @Profile("insurance")
    public Invariant<InsuranceContract> earlyPayout(){
        return new EarlyPayout();
    }
    @Bean
    @Profile("bank")
    public Invariant<BankContract> nonNegativeBalance(){
        return new NonNegativeBalance();
    }
}
