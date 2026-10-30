package com.acme.retail.agents.ch09.routing;

import java.util.Locale;

/**
 * Classifies a request exactly once, then dispatches to exactly one
 * handler -- no loop, no re-evaluation, no multi-step plan. This is the
 * right shape whenever a request never genuinely needs more than one
 * decision to answer, which describes most of what a real system
 * actually receives.
 */
public class RequestCategoryClassifier {

    public RequestCategory classify(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);

        if (normalized.contains("eligible") || normalized.contains("return")) {
            return RequestCategory.RETURN_ELIGIBILITY;
        }
        if (normalized.contains("status") || normalized.contains("where is my order")) {
            return RequestCategory.ORDER_STATUS;
        }
        return RequestCategory.UNKNOWN;
    }
}
