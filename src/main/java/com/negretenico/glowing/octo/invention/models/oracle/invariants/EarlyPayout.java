package com.negretenico.glowing.octo.invention.models.oracle.invariants;

import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;
import com.negretenico.glowing.octo.invention.models.oracle.Oracle;
import com.negretenico.glowing.octo.invention.models.oracle.OracleValidationException;

public record EarlyPayout() implements Oracle<InsuranceContract> {
	@Override
	public boolean check(InsuranceContract contract) {
		if (!contract.hasMeetPremium() && contract.claim()) {
			throw new OracleValidationException("EarlyPayout: Tried to claim " +
					"premium early.");
		}
		return true;
	}
}
