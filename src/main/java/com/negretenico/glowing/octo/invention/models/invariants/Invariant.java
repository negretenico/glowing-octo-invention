package com.negretenico.glowing.octo.invention.models.invariants;

import com.common.functionico.evaluation.Result;
import com.negretenico.glowing.octo.invention.models.contracts.Contract;

public interface Invariant <C extends Contract<?>> {
    Result<Void> check(C contract);
}
