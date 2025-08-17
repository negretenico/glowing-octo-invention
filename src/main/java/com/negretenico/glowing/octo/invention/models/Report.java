package com.negretenico.glowing.octo.invention.models;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String contractType;
    private int iterations;
    private List<String> invariantChecks = new ArrayList<>();
    private String finalState;

    public Report contractType(String type) {
        this.contractType = type;
        return this;
    }

    public Report iterations(int count) {
        this.iterations = count;
        return this;
    }

    public void addInvariantCheck(String check) {
        this.invariantChecks.add(check);
    }

    public Report finalState(String state) {
        this.finalState = state;
        return this;
    }

    @Override
    public String toString() {
        return "Contract Report\n" +
                "Contract Type: " + contractType + "\n" +
                "Iterations: " + iterations + "\n" +
                "Invariant Checks:\n" + String.join("\n", invariantChecks) + "\n" +
                "Final State: " + finalState + "\n";
    }
}
