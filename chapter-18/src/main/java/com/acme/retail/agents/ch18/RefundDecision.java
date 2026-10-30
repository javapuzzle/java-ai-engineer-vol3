package com.acme.retail.agents.ch18;

public record RefundDecision(boolean allowed, String reason) {

    public static RefundDecision permit() {
        return new RefundDecision(true, "Refund approved.");
    }

    public static RefundDecision blocked(String reason) {
        return new RefundDecision(false, reason);
    }
}
