package com.negretenico.glowing.octo.invention.config;

import com.common.functionico.risky.Try;
import com.negretenico.glowing.octo.invention.models.Web3jContractParams;
import com.negretenico.glowing.octo.invention.models.Web3jProperties;
import com.negretenico.glowing.octo.invention.models.contracts.Bank;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
@Profile("web3jbank")
public class Web3jConfig {
	private final Web3jProperties web3jProperties;

	public Web3jConfig(Web3jProperties web3jProperties) {
		this.web3jProperties = web3jProperties;
	}

	@Bean
	public Web3j web3j() {
		return Try.of(() -> Web3j.build(new HttpService(web3jProperties.host()))).getOrElse(null);
	}

	@Bean
	public Credentials web3JCredentials() {
		return Credentials.create(web3jProperties.pk());
	}

	@Bean
	public ContractGasProvider gasProvider() {
		return new DefaultGasProvider();
	}

	@Bean
	public Web3jContractParams contractParams(
			Web3j web3j,
			Credentials web3JCredentials,
			ContractGasProvider gasProvider
	) {
		return new Web3jContractParams(web3j, web3JCredentials,
				web3jProperties.contractAddress(), gasProvider);
	}

	@Bean
	public Bank loadBank(Web3jContractParams contractParams) {
		return Bank.load(contractParams.contractAddress(),
				contractParams.client(),
				contractParams.credentials(),
				contractParams.contractGasProvider());
	}
}
