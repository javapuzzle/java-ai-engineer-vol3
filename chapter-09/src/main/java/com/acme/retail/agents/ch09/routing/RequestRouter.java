package com.acme.retail.agents.ch09.routing;

import com.acme.retail.agents.ch09.OrderDirectory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dispatches a request to exactly one handler, based on one upfront
 * classification -- never re-evaluates, never loops. Contrast this with
 * Chapter 4's {@code ReactLoop}, which re-consults its decision-maker
 * after every observation: routing is the right tool when a request
 * genuinely never needs more than one decision to resolve.
 */
@Service
public class RequestRouter {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");

    private final RequestCategoryClassifier classifier;
    private final OrderDirectory orderDirectory;

    public RequestRouter(RequestCategoryClassifier classifier, OrderDirectory orderDirectory) {
        this.classifier = classifier;
        this.orderDirectory = orderDirectory;
    }

    public String route(String request) {
        RequestCategory category = classifier.classify(request);
        String orderId = extractOrderId(request);

        return switch (category) {
            case ORDER_STATUS -> orderDirectory.findById(orderId)
                    .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                    .orElse("No order found with ID " + orderId + ".");
            case RETURN_ELIGIBILITY -> orderDirectory.findById(orderId)
                    .map(order -> order.status().equals("CANCELLED")
                            ? "Order " + orderId + " is not eligible for return."
                            : "Order " + orderId + " is eligible for return.")
                    .orElse("No order found with ID " + orderId + ".");
            case UNKNOWN -> "I'm not able to help with that request yet.";
        };
    }

    private String extractOrderId(String request) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(request);
        return matcher.find() ? matcher.group() : "";
    }
}
