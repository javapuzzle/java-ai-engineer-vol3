package com.acme.retail.agents.ch19;

/**
 * One entry in a connected, multi-agent trace -- which agent did what,
 * and what kind of thing it did (reasoning, a tool call, or an
 * inter-agent message). Ordering across agents is preserved by
 * insertion order in {@link AgentTrace}, not reconstructed after the
 * fact from timestamps in scattered per-agent logs.
 */
public record TraceEvent(String agentName, String eventType, String description) {
}
