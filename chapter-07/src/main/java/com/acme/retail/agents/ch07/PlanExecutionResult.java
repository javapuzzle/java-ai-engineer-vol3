package com.acme.retail.agents.ch07;

import java.util.List;

/** Whether the whole plan actually completed, and the real outcome of every task it reached. */
public record PlanExecutionResult(boolean completed, List<TaskOutcome> outcomes, String summary) {
}
