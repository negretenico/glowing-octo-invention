package com.negretenico.glowing.octo.invention.models.contracts;

import com.common.functionico.evaluation.Result;
import com.common.functionico.risky.Try;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigInteger;

public record Web3jBankContract(Bank bank, Convert.Unit unit)  implements Contract<BigInteger> {

    @Override
    public Result<BigInteger> add(BigInteger deposit) {
        BigInteger depositAmount =
                Convert.toWei(deposit.toString(),unit).toBigInteger();
        Try<TransactionReceipt> tr =
                Try.of(()->bank.deposit(depositAmount).send());
        if(tr.isFailure()){
            return Result.failure(String.format("Web3jBankContract: could not" +
                    " send %s to address %s",depositAmount,
                    bank.getContractAddress()));
        }
        return balance();
    }

    @Override
    public Result<BigInteger> withdraw(BigInteger amount) {
        BigInteger weiAmount =
                Convert.toWei(amount.toString(),unit).toBigInteger();
        Try<TransactionReceipt> tryTr = Try.of(()->bank.withdraw(weiAmount).send());
        if(tryTr.isFailure()){
            return  Result.failure(String.format("Web3jBankContract: Could " +
                    "not withdraw amount %s from address %s",weiAmount,bank.getContractAddress()));
        }
        return balance();
    }
    public Result<BigInteger> balance(){
        Try<BigInteger> tryReceipt = Try.of(()->bank.getBalance().send());
        if(tryReceipt.isFailure()){
            return Result.failure(String.format("Could not get the balance of" +
                    " account %s",bank.getContractAddress()));
        }
        return  Result.success(tryReceipt.getOrElse(BigInteger.ZERO));
    }
}
