package com.negretenico.glowing.octo.invention.models;

import org.springframework.util.function.ThrowingSupplier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class OperationResult<State> {
	private final State state;
	private final Throwable e;

	private OperationResult(State state, Throwable e) {
		this.state = state;
		this.e = e;
	}

	public static <State> OperationResult<State> success(State initial) {
		return new OperationResult<>(initial, null);
	}

	public static <State> OperationResult<State> failure(Throwable e) {
		return new OperationResult<>(null, e);
	}

	public OperationResult<State> onSuccess(Consumer<State> success) {
		if (Objects.isNull(this.e)) {
			success.accept(this.state);
		}
		return this;
	}

	public static <State> OperationResult<State> of(ThrowingSupplier<State> func) {
		try {
			return success(func.get());
		} catch (Exception e) {
			return failure(e);
		}
	}

	public OperationResult<State> onFailure(Consumer<Throwable> failure) {
		if (Objects.nonNull(this.e)) {
			failure.accept(this.e);
		}
		return this;
	}

	public <NewState> OperationResult<NewState> flatMap(Function<State, OperationResult<NewState>> next) {
		if (Objects.nonNull(e)) {
			return new OperationResult<>(null, e);
		}
		try {
			return next.apply(state);
		} catch (Exception e) {
			return new OperationResult<>(null, e);
		}
	}


	public <NewState> OperationResult<NewState> map(Function<State, NewState> convert) {
		if (Objects.nonNull(e)) {
			return new OperationResult<>(null, e);
		}
		if (Objects.isNull(state)) {
			return new OperationResult<>(null, new RuntimeException("Cannot map on null state"));
		}
		try {
			NewState newState = convert.apply(this.state);
			if (newState == null) {
				throw new RuntimeException("Map function returned null");
			}
			return new OperationResult<>(newState, null);
		} catch (Exception e) {
			return new OperationResult<>(null, e);
		}
	}

	public Throwable getError() {
		return e;
	}

	public State get() {
		return state;
	}

	public State getOrElse(State fallback) {
		return Objects.nonNull(state) ? state : fallback;
	}
}
