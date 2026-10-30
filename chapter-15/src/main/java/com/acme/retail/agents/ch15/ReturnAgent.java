package com.acme.retail.agents.ch15;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * A specialist agent with exactly one job: decide whether an order is
 * eligible for return. It has no idea what a replacement item is, or
 * whether one is in stock -- again, only the orchestrator connects
 * this specialist's answer to any other one's.
 */
@Service
public class ReturnAgent {

    private static final Set<String> NON_RETURNABLE_CATEGORIES = Set.of("APPAREL");

    private final OrderDirectory orderDirectory;

    public ReturnAgent(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    public boolean isEligibleForReturn(String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> "SHIPPED".equals(order.status()) && !NON_RETURNABLE_CATEGORIES.contains(order.category()))
                .orElse(false);
    }

    public String explainEligibility(String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> isEligibleForReturn(orderId)
                        ? "Order " + orderId + " is eligible for return."
                        : "Order " + orderId + " is not eligible for return (status: " + order.status() + ", category: " + order.category() + ").")
                .orElse("No order found with ID " + orderId + ".");
    }
}
