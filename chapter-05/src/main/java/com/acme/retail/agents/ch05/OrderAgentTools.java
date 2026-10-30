package com.acme.retail.agents.ch05;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * Every tool this chapter's agent has access to, consolidated from
 * Chapters 2-4: a one-hop status lookup, and a two-hop return-eligibility
 * path. The agent decides, per question, which shape applies -- none of
 * these methods know anything about that decision.
 */
@Service
public class OrderAgentTools {

    private final OrderDirectory orderDirectory;

    public OrderAgentTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Look up the last known status Acme Retail recorded for a customer's order, by order ID.")
    public String getOrderStatus(
            @ToolParam(description = "The Acme Retail order ID, formatted like ORD-1234.") String orderId) {

        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
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
