import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

export default buildModule("BankModule", (m) => {
  const counter = m.contract("Bank");

  m.call(counter, "deposit", [], {
    value: 500n,
  });
  m.call(counter, "withdraw", [5n]);
  m.call(counter, "getBalance", []);

  return { counter };
});
