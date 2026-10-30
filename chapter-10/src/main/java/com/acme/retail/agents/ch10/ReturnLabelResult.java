package com.acme.retail.agents.ch10;

/**
 * What the reflection step actually decided: keep the draft, replace it
 * with a better match, or -- honestly -- admit neither candidate is
 * confidently right and ask for human review instead of guessing.
 */
public record ReturnLabelResult(boolean needsHumanReview, OrderItem item, String explanation) {

    public static ReturnLabelResult confirmed(OrderItem item, String explanation) {
        return new ReturnLabelResult(false, item, explanation);
    }

    public static ReturnLabelResult revised(OrderItem item, String explanation) {
        return new ReturnLabelResult(false, item, explanation);
    }

    public static ReturnLabelResult needsReview(String explanation) {
        return new ReturnLabelResult(true, null, explanation);
    }
}
