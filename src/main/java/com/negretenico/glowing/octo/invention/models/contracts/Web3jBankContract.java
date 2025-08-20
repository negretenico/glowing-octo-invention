package com.negretenico.glowing.octo.invention.models.contracts;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.OperationResult;
import com.negretenico.glowing.octo.invention.models.Report;
import com.negretenico.glowing.octo.invention.models.oracle.OracleValidationException;
import org.springframework.util.function.ThrowingSupplier;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public record Web3jBankContract(Bank bank, Convert.Unit unit, Report report) {

	private void handle(ThrowingSupplier<TransactionReceipt> f) {
		Consumer<String> addToOracleReport = report::addOracleCheck;
		String passed = ": ✅ Passed";
		String failed = ":❌ Failed -";
		OperationResult.of(f).map(receipt -> {
					if (Objects.isNull(receipt)) {
						throw new RuntimeException("Transaction receipt is null");
					}
					BigInteger gasThreshold = BigInteger.valueOf(10_000_000);
					if (receipt.getGasUsed().compareTo(gasThreshold) > 0) {
						throw new OracleValidationException(String.format("Web3jBankContract:" +
								" Used too much gas: %s", receipt.getGasUsed()));
					}
					String message = String.format("Gas used: %s (threshold: %s )",
							receipt.getGasUsed(), gasThreshold);
					System.out.println(message);
					return receipt;
				})
				.onSuccess(receipt -> {
					String message = "Oracle.GasCheck-> TX: %s %s";
					addToOracleReport.accept(String.format(message,
							receipt.getTransactionIndexRaw(), passed));
				})
				.onFailure(e -> {
					String message = "Oracle.GasCheck-> %s %s";
					addToOracleReport.accept(String.format(message, failed, e.getMessage()));
				})
				.map(receipt -> balance())
				.map(this::nonNegativeCheck)
				.onSuccess(s -> {
					String message = "Oracle.InvarianceCheck-> %s";
					addToOracleReport.accept(String.format(message, passed));
				})
				.onFailure(e -> {
					String message = "Oracle.InvarianceCheck-> %s %s";
					addToOracleReport.accept(String.format(message, failed, e.getMessage()));
				});
	}

	public void fuzz(int rnd, BigInteger amount, Web3j web3j, Credentials credentials) {
		if (rnd > 0 && rnd <= 24) {
			withdraw(amount);
			return;
		} else if (rnd > 24 && rnd < 75) {
			balance();
			return;
		}
		add(amount, web3j, credentials);
	}

	private void add(BigInteger deposit, Web3j web3j, Credentials credentials) {
		BigInteger depositAmount = Convert.toWei(deposit.toString(), unit).toBigInteger();
		BigInteger gas = OperationResult
				.of(() -> web3j.ethGasPrice().send().getGasPrice())
				.onFailure(e -> System.out.printf("Failed to get gas %s", e.getMessage()))
				.getOrElse(BigInteger.valueOf(20_000_000_000L));
		Function depositFunction = new Function(
				"deposit",
				Collections.emptyList(),
				Collections.emptyList()
		);
		BigInteger gasLimit = BigInteger.valueOf(100_000);
		String encodedFunction = FunctionEncoder.encode(depositFunction);
		BigInteger nonce = OperationResult.of(() -> web3j.ethGetTransactionCount(
						credentials.getAddress(),
						DefaultBlockParameterName.LATEST
				).send().getTransactionCount())
				.onFailure(e -> {
					throw new RuntimeException();
				}).get();
		RawTransaction rawTransaction = RawTransaction.createTransaction(
				nonce,
				gas,
				gasLimit,
				bank.getContractAddress(),
				depositAmount,
				encodedFunction
		);
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		EthSendTransaction response =
				OperationResult.of(() -> web3j.ethSendRawTransaction(hexValue).send())
						.onSuccess(ethSendTransaction -> {
							if (!ethSendTransaction.hasError()) {
								return;
							}
							throw new RuntimeException("Successfully got the transaction " +
									"however it had errors");
						})
						.onFailure(e -> {
							throw new RuntimeException("Failed to send transaction: " + e.getMessage(), e);
						}).get();
		TransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(
				web3j,
				TransactionManager.DEFAULT_POLLING_FREQUENCY,
				TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
		);
		OperationResult.of(() -> processor.waitForTransactionReceipt(response.getTransactionHash()))
				.onSuccess(receipt -> {
					if (Objects.isNull(receipt)) {
						throw new RuntimeException("Transaction receipt is null for hash: " + response.getTransactionHash());
					}
					if (!"0x1".equals(receipt.getStatus())) {
						throw new RuntimeException("Transaction failed with status: " + receipt.getStatus());
					}
					handle(() -> receipt);
				})
				.onFailure(e -> {
					throw new RuntimeException("Failed to add deposit: " + e.getMessage(), e);
				});
	}

	private boolean nonNegativeCheck(Result<BigInteger> res) throws OracleValidationException {
		if (Objects.isNull(res) || Objects.isNull(res.data())) {
			throw new OracleValidationException("Web3jNonNegativeBalance: Balance result is null");
		}
		if (res.data().compareTo(BigInteger.ZERO) < 0) {
			throw new OracleValidationException("Web3jNonNegativeBalance: Balance is under 0: " + res.data());
		}
		System.out.println("Balance check passed: " + res.data());
		return true;
	}

	private void withdraw(BigInteger amount) {
		BigInteger withdrawAmount = Convert.toWei(amount.toString(), unit).toBigInteger();
		OperationResult.of(() -> {
					TransactionReceipt receipt = bank.withdraw(withdrawAmount).send();
					if (Objects.isNull(receipt)) {
						throw new RuntimeException("Withdraw receipt is null");
					}
					if (!"0x1".equals(receipt.getStatus())) {
						throw new RuntimeException(String.format("Withdraw transaction " +
								"failed with status: %s", receipt.getStatus()));
					}
					return receipt;
				}).onSuccess(receipt -> handle(() -> receipt))
				.onFailure(e -> System.out.printf("Withdraw failed: %s", e.getMessage()));
	}

	private Result<BigInteger> balance() {
		java.util.function.Function<BigInteger, Result<BigInteger>> mapper =
				balance -> Result.success(balance != null ? balance : BigInteger.ZERO);
		return OperationResult.of(() -> bank.getBalance().send())
				.map(mapper)
				.getOrElse(Result.failure("Could not get the balance of account "));
	}
}