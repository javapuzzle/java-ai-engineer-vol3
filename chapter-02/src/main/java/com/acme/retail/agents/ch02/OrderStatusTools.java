package com.acme.retail.agents.ch02;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * The first real tool Acme Retail's assistant can be given. {@code
 * @Tool}'s {@code description} is not documentation for a human reader --
 * it is the only text a model ever sees to decide whether and how to call
 * this method. {@code @ToolParam}'s description is the same contract,
 * one level down, for a single argument. Chapter 3 is entirely about
 * getting these two strings right.
 */
@Service
public class OrderStatusTools {

    private final OrderDirectory orderDirectory;

    public OrderStatusTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Look up the last known status Acme Retail recorded for a customer's order, by order ID.")
    public String getOrderStatus(
            @ToolParam(description = "The Acme Retail order ID, formatted like ORD-1234.") String orderId) {

        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }
}
