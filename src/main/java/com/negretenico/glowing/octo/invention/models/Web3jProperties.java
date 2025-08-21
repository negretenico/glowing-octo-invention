package com.negretenico.glowing.octo.invention.models;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("web3jbank")
public record Web3jProperties(String host,
															String contractAddress,
															String account,
															String pk) {
	@PostConstruct
	void init() {
		System.out.println("This is the value we got from the configuration " + contractAddress);
	}
}
