package com.acme.retail.agents.ch04;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A deterministic stand-in for what a live model's reasoning would
 * produce for exactly one scenario: "is this order eligible for
 * expedited return, and what's the window?" A real system replaces this
 * whole class with a ChatClient call; nothing about {@link ReactLoop} or
 * the tools it calls needs to change when that happens.
 */
public class ScriptedReturnEligibilityPlanner implements NextStepPlanner {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("Category: (\\w+)\\.");

    @Override
    public PlannedAction decideNext(String question, List<ReactStep> history) {
        if (history.isEmpty()) {
            String orderId = extractOrderId(question);
            return PlannedAction.callTool(
                    "I need to know if this order is even eligible before I can find a return window.",
                    "checkReturnEligibility",
                    java.util.Map.of("orderId", orderId));
        }

        ReactStep lastStep = history.get(history.size() - 1);

        if (history.size() == 1) {
            if (lastStep.observation().contains("not eligible")) {
                return PlannedAction.finish(lastStep.observation());
            }
            String category = extractCategory(lastStep.observation());
            return PlannedAction.callTool(
                    "It's eligible. Now I need the return window for its category.",
                    "getReturnWindowDays",
                    java.util.Map.of("category", category));
        }

        ReactStep eligibilityStep = history.get(0);
        return PlannedAction.finish(eligibilityStep.observation() + " " + lastStep.observation());
    }

    private String extractOrderId(String question) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(question);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No order ID found in question: " + question);
        }
        return matcher.group();
    }

    private String extractCategory(String eligibilityObservation) {
        Matcher matcher = CATEGORY_PATTERN.matcher(eligibilityObservation);
        if (!matcher.find()) {
            throw new IllegalStateException("Expected a category in: " + eligibilityObservation);
        }
        return matcher.group(1);
    }
}
