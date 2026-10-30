package com.acme.retail.agents.ch09.chaining;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * A fixed sequence of steps, each one's output feeding directly into the
 * next -- no decision-maker, no branching, no loop. This is the simplest
 * possible workflow shape, and the right one whenever a request's
 * processing order is always the same, regardless of what any step
 * actually finds.
 */
public class PromptChain {

    private final List<UnaryOperator<String>> steps;

    public PromptChain(List<UnaryOperator<String>> steps) {
        this.steps = steps;
    }

    public String run(String input) {
        String current = input;
        for (UnaryOperator<String> step : steps) {
            current = step.apply(current);
        }
        return current;
    }
}
