package com.acme.retail.agents.ch07;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * The four independent actions "process this return end to end" actually
 * requires. None of them knows it's part of a larger plan -- {@link
 * ReturnProcessingPlanner} decides the order, and {@link PlanExecutor}
 * carries it out.
 */
@Service
public class ReturnProcessingTools {

    private final OrderDirectory orderDirectory;

    public ReturnProcessingTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Check whether an order is currently eligible for a return, by order ID.")
    public String checkReturnEligibility(@ToolParam(description = "The order ID, formatted like ORD-1234.") String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> order.status().equals("CANCELLED")
                        ? "Order " + orderId + " is not eligible for return."
                        : "Order " + orderId + " is eligible for return.")
                .orElse("No order found with ID " + orderId + ".");
    }

    @Tool(description = "Generate a return shipping label for an order, by order ID.")
    public String generateReturnLabel(@ToolParam(description = "The order ID, formatted like ORD-1234.") String orderId) {
        return "Return label RL-" + orderId + " generated for order " + orderId + ".";
    }

    @Tool(description = "Notify the customer that their return label is ready, by order ID.")
    public String notifyCustomer(@ToolParam(description = "The order ID, formatted like ORD-1234.") String orderId) {
        return "Customer for order " + orderId + " notified that the return label is ready.";
    }

    @Tool(description = "Restock inventory for the product category associated with a returned order, by order ID.")
    public String restockInventory(@ToolParam(description = "The order ID, formatted like ORD-1234.") String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> "Inventory for category " + order.category() + " updated after the return.")
                .orElse("No order found with ID " + orderId + ".");
    }
}
