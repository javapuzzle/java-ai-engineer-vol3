package com.acme.retail.agents.ch20;

public record ReorderDecision(boolean allowed, String reason) {

    public static ReorderDecision permit() {
        return new ReorderDecision(true, "Reorder approved.");
    }

    public static ReorderDecision blocked(String reason) {
        return new ReorderDecision(false, reason);
    }
}
