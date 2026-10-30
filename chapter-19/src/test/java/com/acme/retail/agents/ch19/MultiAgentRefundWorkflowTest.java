package com.acme.retail.agents.ch19;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MultiAgentRefundWorkflowTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final AgentTrace trace = new AgentTrace("TRACE-1", meterRegistry);
    private final RefundGuardrail refundGuardrail = new RefundGuardrail(new OrderDirectory());
    private final RefundAgent refundAgent = new RefundAgent(refundGuardrail);
    private final MultiAgentRefundWorkflow workflow = new MultiAgentRefundWorkflow(refundAgent);

    @Test
    void theTraceReconstructsWhyTheSecondRefundWasBlockedInOneConnectedSequence() {
        workflow.handleTwoPartRefundRequest(trace, "ORD-7731", new BigDecimal("100.00"), new BigDecimal("100.00"));

        List<TraceEvent> events = trace.events();
        assertThat(events).extracting(TraceEvent::eventType)
                .containsExactly("reasoning", "tool_call", "guardrail_decision", "tool_call", "guardrail_decision", "reasoning");
        assertThat(events.get(2).description()).isEqualTo("allowed");
        assertThat(events.get(4).description()).contains("blocked");
        assertThat(events.get(5).description()).contains("Second refund was blocked");
    }

    @Test
    void renderProducesOneLegibleConnectedNarrativeAcrossBothAgents() {
        workflow.handleTwoPartRefundRequest(trace, "ORD-7731", new BigDecimal("100.00"), new BigDecimal("100.00"));

        String rendered = trace.render();

        assertThat(rendered).contains("[OrderAgent]");
        assertThat(rendered).contains("[RefundAgent]");
        assertThat(rendered).contains("blocked");
    }

    @Test
    void bothRefundsSucceedingProducesNoBlockedEventsAtAll() {
        String summary = workflow.handleTwoPartRefundRequest(trace, "ORD-9042", new BigDecimal("50.00"), new BigDecimal("50.00"));

        assertThat(summary).isEqualTo("Both refunds were processed.");
        assertThat(trace.events()).extracting(TraceEvent::description).noneMatch(d -> d.contains("blocked"));
    }
}
