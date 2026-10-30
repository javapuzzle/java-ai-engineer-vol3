package com.acme.retail.agents.ch07;

/** One planned task, paired with what actually happened when it ran. */
public record TaskOutcome(PlannedTask task, String observation) {
}
