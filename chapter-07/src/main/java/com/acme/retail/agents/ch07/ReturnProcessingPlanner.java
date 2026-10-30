package com.acme.retail.agents.ch07;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A deterministic stand-in for a live model's planning, scripted for
 * exactly one request shape: "process this return end to end." The plan
 * it produces is always the same four steps, in the same order --
 * eligibility first, because every later step is pointless if that one
 * fails.
 */
public class ReturnProcessingPlanner implements TaskPlanner {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");

    @Override
    public TaskPlan plan(String request) {
        String orderId = extractOrderId(request);

        return new TaskPlan(List.of(
                new PlannedTask("Confirm the order is actually eligible before doing anything else.",
                        "checkReturnEligibility", Map.of("orderId", orderId)),
                new PlannedTask("Generate the return shipping label.",
                        "generateReturnLabel", Map.of("orderId", orderId)),
                new PlannedTask("Let the customer know the label is ready.",
                        "notifyCustomer", Map.of("orderId", orderId)),
                new PlannedTask("Restock inventory now that the return is initiated.",
                        "restockInventory", Map.of("orderId", orderId))
        ));
    }

    private String extractOrderId(String request) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(request);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No order ID found in request: " + request);
        }
        return matcher.group();
    }
}
