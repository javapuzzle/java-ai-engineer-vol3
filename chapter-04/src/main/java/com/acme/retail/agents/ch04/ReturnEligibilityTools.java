package com.acme.retail.agents.ch04;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * Two independent tools that together answer a question no single call
 * can: "is this order eligible for expedited return, and if so, what's
 * the window?" Neither tool knows the other exists -- the loop in
 * {@link ReactLoop} is what chains them.
 */
@Service
public class ReturnEligibilityTools {

    private final OrderDirectory orderDirectory;

    public ReturnEligibilityTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Check whether an order is currently eligible for an expedited return, by order ID.")
    public String checkReturnEligibility(
            @ToolParam(description = "The Acme Retail order ID, formatted like ORD-1234.") String orderId) {

        return orderDirectory.findById(orderId)
                .map(order -> order.status().equals("CANCELLED")
                        ? "Order " + orderId + " is not eligible for expedited return."
                        : "Order " + orderId + " is eligible for expedited return. Category: " + order.category() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }

    @Tool(description = "Look up the expedited return window, in days, for a product category.")
    public String getReturnWindowDays(
            @ToolParam(description = "The product category, e.g. ELECTRONICS or APPAREL.") String category) {

        return switch (category) {
            case "ELECTRONICS" -> "The expedited return window for ELECTRONICS is 15 days.";
            case "APPAREL" -> "The expedited return window for APPAREL is 30 days.";
            default -> "No known return window for category " + category + ".";
        };
    }
}
