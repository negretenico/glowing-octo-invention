package com.negretenico.glowing.octo.invention.models.invariants;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.InsuranceContract;

public record EarlyPayout()implements Invariant<InsuranceContract> {
    @Override
    public Result<Void> check(InsuranceContract contract) {
        return contract.hasMeetPremium() || !contract.claim()?
                Result.success(null):
                Result.failure("EarlyPayout: Tried to claim premium early.");
    }
}
