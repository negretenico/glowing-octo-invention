package com.negretenico.glowing.octo.invention.models.oracle;

import com.negretenico.glowing.octo.invention.models.contracts.Contract;

public interface Oracle<C extends Contract<?>> {
	boolean check(C contract) throws OracleValidationException;
}
