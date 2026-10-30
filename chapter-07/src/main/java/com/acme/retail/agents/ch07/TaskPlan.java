package com.acme.retail.agents.ch07;

import java.util.List;

/**
 * The whole ordered sequence of tasks, decided once, upfront -- the
 * opposite of Chapter 4's ReAct loop, which decides one step at a time as
 * observations arrive. Being able to print or log this list in full,
 * before a single tool has run, is exactly what an interleaved ReAct loop
 * cannot offer.
 */
public record TaskPlan(List<PlannedTask> tasks) {
}
