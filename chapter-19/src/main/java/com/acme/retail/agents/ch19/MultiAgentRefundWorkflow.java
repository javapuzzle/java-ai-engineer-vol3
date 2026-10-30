package com.acme.retail.agents.ch19;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Coordinates a two-part refund request across two calls to
 * {@link RefundAgent}, recording its own reasoning into the same shared
 * trace the specialist writes to -- the whole point being one connected
 * narrative, not two agents' independent logs that have to be manually
 * reconciled after the fact.
 */
@Service
public class MultiAgentRefundWorkflow {

    private final RefundAgent refundAgent;

    public MultiAgentRefundWorkflow(RefundAgent refundAgent) {
        this.refundAgent = refundAgent;
    }

    public String handleTwoPartRefundRequest(AgentTrace trace, String orderId, BigDecimal firstAmount, BigDecimal secondAmount) {
        trace.record("OrderAgent", "reasoning", "Customer requested two partial refunds on " + orderId);

        RefundDecision first = refundAgent.attemptRefund(trace, orderId, firstAmount);
        RefundDecision second = refundAgent.attemptRefund(trace, orderId, secondAmount);

        String summary = second.allowed()
                ? "Both refunds were processed."
                : "Second refund was blocked: " + second.reason();
        trace.record("OrderAgent", "reasoning", summary);
        return summary;
    }
}
