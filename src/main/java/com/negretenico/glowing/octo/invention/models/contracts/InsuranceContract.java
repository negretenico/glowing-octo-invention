package com.negretenico.glowing.octo.invention.models.contracts;

import com.common.functionico.evaluation.Result;

import java.math.BigInteger;

public record InsuranceContract(BigInteger premiumAmountPaid, BigInteger coverage,
                                boolean claim,
                                BigInteger minPremiumAcceptableBeforePayout) implements Contract<BigInteger> {
    @Override
    public Result<InsuranceContract> add(BigInteger deposit) {
        return Result.success(new InsuranceContract(premiumAmountPaid.add(deposit),
                coverage,claim,minPremiumAcceptableBeforePayout));
    }

    @Override
    public Result<InsuranceContract> withdraw(BigInteger amount) {
        return Result.failure("InsuranceContract: No withdrawing");
    }
    public InsuranceContract claimEvent(){
        return new InsuranceContract(premiumAmountPaid, coverage,true,minPremiumAcceptableBeforePayout);
    }
    public  boolean hasMeetPremium(){
        return premiumAmountPaid.compareTo(minPremiumAcceptableBeforePayout) ==0;
    }
    public Result<BigInteger> payout(){
        if(!claim){
            return Result.failure("InsuranceContract: Event has not occurred " +
                    "yet");
        }
        if(premiumAmountPaid.compareTo(minPremiumAcceptableBeforePayout)<0){
            return Result.failure("InsuranceContract: didn't reach premiumAmountPaid");
        }
        return Result.success(coverage);
    }
}
