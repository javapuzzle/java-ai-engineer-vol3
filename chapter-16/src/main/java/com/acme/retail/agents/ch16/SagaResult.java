package com.acme.retail.agents.ch16;

/**
 * The outcome of running (or resuming) a saga -- the final state it
 * reached, and a human-readable summary of what happened, including
 * whether compensation was needed.
 */
public record SagaResult(WorkflowState finalState, String summary) {
}
