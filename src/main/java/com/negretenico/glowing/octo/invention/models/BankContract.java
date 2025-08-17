package com.negretenico.glowing.octo.invention.models;

import com.common.functionico.evaluation.Result;

import java.math.BigInteger;

public record BankContract(BigInteger balance) implements Contract<BigInteger> {

    @Override
    public Result<BankContract> add(BigInteger deposit) {
        return Result.success(new BankContract(balance.add(deposit)));
    }

    @Override
    public Result<BankContract> withdraw(BigInteger amount) {
        if(amount.compareTo(balance)>0){
            return Result.failure("BankContract: Underflow");
        }
        return Result.success(new BankContract(balance.subtract(amount)));
    }
}
