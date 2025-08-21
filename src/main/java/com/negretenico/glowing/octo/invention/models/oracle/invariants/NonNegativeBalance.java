package com.negretenico.glowing.octo.invention.models.oracle.invariants;

import com.negretenico.glowing.octo.invention.models.contracts.BankContract;
import com.negretenico.glowing.octo.invention.models.oracle.Oracle;
import com.negretenico.glowing.octo.invention.models.oracle.OracleValidationException;

import java.math.BigInteger;

public record NonNegativeBalance() implements Oracle<BankContract> {
	@Override
	public boolean check(BankContract contract) {
		if (contract.balance().compareTo(BigInteger.ZERO) >= 0) {
			throw new OracleValidationException("NonNegativeBalance: Balance is under 0");
		}
		return true;
	}
}
