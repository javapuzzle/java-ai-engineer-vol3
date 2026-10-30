package com.acme.retail.agents.ch19;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One shared, connected trace that every agent in a workflow appends to
 * -- deliberately not a separate log per agent. A multi-agent decision
 * becomes legible after the fact only if its steps can be read back in
 * the order they actually happened, across every agent involved, not
 * reconstructed by manually interleaving timestamps from scattered logs.
 * Every recorded event is also mirrored into a real Micrometer
 * {@link Counter}, tagged by agent and event type -- extending Spring
 * AI's own observation/Micrometer discipline to agent-specific spans.
 */
public class AgentTrace {

    private final String traceId;
    private final MeterRegistry meterRegistry;
    private final List<TraceEvent> events = new ArrayList<>();

    public AgentTrace(String traceId, MeterRegistry meterRegistry) {
        this.traceId = traceId;
        this.meterRegistry = meterRegistry;
    }

    public void record(String agentName, String eventType, String description) {
        events.add(new TraceEvent(agentName, eventType, description));
        Counter.builder("agent.trace.events")
                .tag("agent", agentName)
                .tag("eventType", eventType)
                .register(meterRegistry)
                .increment();
    }

    public String traceId() {
        return traceId;
    }

    public List<TraceEvent> events() {
        return List.copyOf(events);
    }

    public String render() {
        return events.stream()
                .map(event -> "[" + event.agentName() + "] " + event.eventType() + ": " + event.description())
                .collect(Collectors.joining("\n"));
    }
}
