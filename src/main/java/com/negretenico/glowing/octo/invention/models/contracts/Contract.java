package com.negretenico.glowing.octo.invention.models.contracts;

import com.common.functionico.evaluation.Result;

public interface Contract<T> {
	Result add(T deposit);

	Result withdraw(T amount);
}
