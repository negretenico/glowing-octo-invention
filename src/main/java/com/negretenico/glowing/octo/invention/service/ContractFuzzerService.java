package com.negretenico.glowing.octo.invention.service;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.Contract;

import java.util.Random;


public class ContractFuzzerService<T, C extends Contract<T>> {
	private C contract; // current state
	private final Random r = new Random();

	public ContractFuzzerService(C contract) {
		this.contract = contract;
	}

	public C getContract() {
		return contract;
	}

	public void updateContract(C updatedContract) {
		contract = updatedContract;
	}

	public void fuzz(T input) {
		Result<C> result = r.nextBoolean()
				? (Result<C>) contract.add(input)
				: (Result<C>) contract.withdraw(input);

		if (result.isFailure()) {
			System.out.println("Failure: " + result.errorMsg());
			return;
		}
		contract = result.data(); // move forward to new state
		System.out.println("Success: " + contract);

	}
}
