package com.negretenico.glowing.octo.invention.config;

import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.oracle.Oracle;
import com.negretenico.glowing.octo.invention.models.oracle.invariants.EarlyPayout;
import com.negretenico.glowing.octo.invention.models.oracle.invariants.NonNegativeBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OracleConfig {
	@Bean
	@Profile("insurance")
	public Oracle<InsuranceContract> earlyPayout() {
		return new EarlyPayout();
	}

	@Bean
	@Profile("bank")
	public Oracle<BankContract> nonNegativeBalance() {
		return new NonNegativeBalance();
	}
}
