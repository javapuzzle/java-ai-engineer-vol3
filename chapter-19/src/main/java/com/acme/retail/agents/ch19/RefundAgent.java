package com.acme.retail.agents.ch19;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Attempts a refund and records both the attempt and the guardrail's
 * decision into the shared trace -- the guardrail decision is exactly
 * the piece of information a scattered per-agent log would have been
 * easy to omit or separate from the call it belongs to.
 */
@Service
public class RefundAgent {

    private final RefundGuardrail refundGuardrail;

    public RefundAgent(RefundGuardrail refundGuardrail) {
        this.refundGuardrail = refundGuardrail;
    }

    public RefundDecision attemptRefund(AgentTrace trace, String orderId, BigDecimal amount) {
        trace.record("RefundAgent", "tool_call", "issueRefund(" + orderId + ", " + amount + ")");
        RefundDecision decision = refundGuardrail.evaluate(new RefundRequest(orderId, amount, false));
        trace.record("RefundAgent", "guardrail_decision",
                decision.allowed() ? "allowed" : "blocked: " + decision.reason());
        return decision;
    }
}
