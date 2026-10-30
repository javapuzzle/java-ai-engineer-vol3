package com.acme.retail.agents.ch19;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentTraceMicrometerTest {

    @Test
    void everyRecordedEventIncrementsARealMicrometerCounterTaggedByAgentAndEventType() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        AgentTrace trace = new AgentTrace("TRACE-2", meterRegistry);

        trace.record("OrderAgent", "reasoning", "Starting workflow");
        trace.record("OrderAgent", "reasoning", "Second reasoning step");
        trace.record("RefundAgent", "tool_call", "issueRefund(ORD-7731, 100.00)");

        Counter reasoningCounter = meterRegistry.get("agent.trace.events")
                .tag("agent", "OrderAgent")
                .tag("eventType", "reasoning")
                .counter();
        Counter toolCallCounter = meterRegistry.get("agent.trace.events")
                .tag("agent", "RefundAgent")
                .tag("eventType", "tool_call")
                .counter();

        assertThat(reasoningCounter.count()).isEqualTo(2.0);
        assertThat(toolCallCounter.count()).isEqualTo(1.0);
    }
}
