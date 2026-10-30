package com.acme.retail.agents.ch08;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decides, per question, which shape applies: a one-hop status lookup, or
 * a two-hop eligibility-then-window question -- and, for the two-hop
 * shape, still knows when the first observation alone is enough to stop
 * (an ineligible order needs no second call). This is the one class a
 * live model's reasoning would actually replace in production; the agent
 * that drives it, and every tool it calls, stays unchanged either way.
 */
public class OrderAgentPlanner implements NextStepPlanner {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("Category: (\\w+)\\.");

    @Override
    public PlannedAction decideNext(String question, List<ReactStep> history) {
        if (history.isEmpty()) {
            return firstStep(question);
        }

        ReactStep lastStep = history.get(history.size() - 1);

        if ("getOrderStatus".equals(lastStep.toolName())) {
            return PlannedAction.finish(lastStep.observation());
        }

        if ("checkReturnEligibility".equals(lastStep.toolName())) {
            if (lastStep.observation().contains("not eligible")) {
                return PlannedAction.finish(lastStep.observation());
            }
            String category = extractCategory(lastStep.observation());
            return PlannedAction.callTool(
                    "It's eligible. Now I need the return window for its category.",
                    "getReturnWindowDays",
                    Map.of("category", category));
        }

        ReactStep eligibilityStep = history.get(0);
        return PlannedAction.finish(eligibilityStep.observation() + " " + lastStep.observation());
    }

    private PlannedAction firstStep(String question) {
        String orderId = extractOrderId(question);
        String normalized = question.toLowerCase(Locale.ROOT);

        if (normalized.contains("eligible") || normalized.contains("return")) {
            return PlannedAction.callTool(
                    "This is a return-eligibility question. I need to check eligibility before anything else.",
                    "checkReturnEligibility",
                    Map.of("orderId", orderId));
        }

        return PlannedAction.callTool(
                "This is a plain status question -- one lookup should be enough.",
                "getOrderStatus",
                Map.of("orderId", orderId));
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
