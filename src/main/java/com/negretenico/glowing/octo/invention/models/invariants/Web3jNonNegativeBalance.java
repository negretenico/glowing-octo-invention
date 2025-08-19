package com.negretenico.glowing.octo.invention.models.invariants;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.Web3jBankContract;

import java.math.BigInteger;
import java.util.function.Function;

public record Web3jNonNegativeBalance() implements Invariant<Web3jBankContract> {
    @Override
    public Result<Void> check(Web3jBankContract contract) {
        Result<BigInteger> res = contract.balance();
        if(res.isFailure()){
            return Result.failure(res.errorMsg());
        }
        return res.data().compareTo(BigInteger.ZERO)<0?
                Result.failure("Web3jNonNegativeBalance: Balance is under 0"):
                Result.success(null);
    }
}
