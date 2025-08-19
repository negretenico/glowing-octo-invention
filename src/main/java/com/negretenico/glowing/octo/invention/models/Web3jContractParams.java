package com.negretenico.glowing.octo.invention.models;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

public record Web3jContractParams(Web3j client,
                                  Credentials credentials,
                                  String contractAddress,
                                  ContractGasProvider contractGasProvider) {
}
