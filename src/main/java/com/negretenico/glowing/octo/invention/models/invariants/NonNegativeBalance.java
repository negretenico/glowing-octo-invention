package com.negretenico.glowing.octo.invention.models.invariants;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.BankContract;

import java.math.BigInteger;

public record NonNegativeBalance() implements Invariant<BankContract> {
    @Override
    public Result<Void> check(BankContract contract) {
        return contract.balance().compareTo(BigInteger.ZERO)<0?
                Result.failure("NonNegativeBalance: Balance is under 0"):
                Result.success(null);
    }
}
