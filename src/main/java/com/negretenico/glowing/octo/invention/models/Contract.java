package com.negretenico.glowing.octo.invention.models;

import com.common.functionico.evaluation.Result;
import org.springframework.boot.availability.ReadinessState;

public interface Contract<T> {
    Result add(T deposit);
    Result withdraw(T amount);
}
