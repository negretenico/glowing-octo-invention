# Glowing Octo Invention

**Java Version:** 21  
**Maven Version:** 3.8.5

---

## Description

**Glowing Octo Invention** is a Java-based smart contract fuzzing and testing framework designed to interact with
Ethereum-based contracts using Web3j. The application allows developers to test both **local contract simulations** and
**deployed contracts**, applying oracle checks and generating detailed fuzzing reports.

The framework supports modular contract testing, including "Bank" and "Insurance" contracts. Fuzzing tests produce
comprehensive reports to help identify unexpected behaviors, gas anomalies, and invariant violations.

---

## Project Structure

* _**src/main/java**_, Java application code
* **_contracts_**, Hardhat project for Solidity contracts
* **_target/fuzz-report_**, Directory where fuzz reports are saved
* **_pom.xml_**, Maven project file with dependencies

---

## Dependencies

- Java 21
- Maven 3.8.5
- Spring Boot
- Web3j
- Hardhat (for deploying local Ethereum contracts)
- Custom utilities: `functionico` (for Result, Try, OperationResult, etc.)

---

## Profiles

The application is profile-driven:

- **bank** → Runs local simulation of the Bank contract.
- **insurance** → Runs local simulation of the Insurance contract.
- **web3jbank** → Runs against a deployed Web3j Bank contract.

Example run:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=bank
mvn spring-boot:run -Dspring-boot.run.profiles=insurance
mvn spring-boot:run -Dspring-boot.run.profiles=web3jbank
```

---
<h2>Running Locally (Simulated Contracts)</h2>

When using local simulations with the bank or insurance profiles, fuzzing is run against mocked contracts.

Fuzz reports will be generated in:

```bash
target/fuzz-report/{report_name}.txt
```

---
<h2>Running Deployed Contracts</h2>

To run against deployed Solidity contracts:

1. Navigate to the contracts folder:
    1. ```cd contracts```
2. Start a local Ethereum node and deploy contracts:
    1. ``` npx hardhat deploy```
    2. ```npx hardhat node```
3. Deploy a specific contract module (example: Bank):
    1. ```npx hardhat ignition deploy ignition\modules\Bank.ts --network localhost```
4. Run the Java application with the deployed profile:
    1. ```mvn spring-boot:run -Dspring-boot.run.profiles=web3jbank```

5. Reports will still be written to:
    1. ```target/fuzz-report/{report_name}.txt```

---
<h2>Notes</h2>

<h3>Fuzzing & Oracles</h3>

* Randomized fuzzing explores unexpected states.
* Gas usage oracle checks for excessive gas consumption.
* Invariant oracle ensures balances never go negative.
* Transaction validation oracle ensures correct function outputs.
* Runs can be deterministic if seeded, enabling reproducibility.

<h3>Contract extensibility</h3>

* To add new contracts:
    * Create a Hardhat module in contracts/ignition/modules.
    * Generate a Web3j Java wrapper.
    * Write fuzzing logic and oracles in Java.

<h3>Centralized error handling</h3>

* Transactions and contract calls are wrapped in OperationResult.
* Allows chaining .onSuccess() and .onFailure() for clean handling.

<h3>Degraded / Legacy notes</h3>

* Web3j auto-generated Bank contract includes @Deprecated constructors.
* Old gas price & limit constructors exist but ContractGasProvider is recommended.
* This repo standardizes on profiles (bank, insurance, web3jbank) to avoid confusion.