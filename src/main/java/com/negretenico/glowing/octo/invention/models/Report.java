package com.negretenico.glowing.octo.invention.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Report {
	private String contractType;
	private int iterations;
	private List<String> oracleChecks = new ArrayList<>();
	private String finalState;

	public Report contractType(String type) {
		this.contractType = type;
		return this;
	}

	public Report iterations(int count) {
		this.iterations = count;
		return this;
	}

	public void addOracleCheck(String check) {
		this.oracleChecks.add(check);
	}

	public Report finalState(String state) {
		this.finalState = state;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(oracleChecks.size());
		IntStream.range(0, oracleChecks.size())
				.forEach(i -> builder.append(String.format("\n%s.%s", i,
						oracleChecks.get(i))));
		return String.format("""
				Contract Report
				Contract Type: %s
				Iterations: %s
				OracleChecks: %s
				Final State: %s
				""", contractType, iterations, builder, finalState);
	}
}
