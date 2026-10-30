package com.acme.retail.agents.ch03;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * The rewritten contract, with the same rigor Sarah would give a public
 * API: a precise description, an explicit parameter contract that states
 * the expected format and rules out the most likely wrong guess (a
 * customer name or email), and defensive validation that checks the
 * argument's shape before ever treating it as a real order ID.
 */
@Service
public class PreciseOrderTools {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");

    private final OrderDirectory orderDirectory;

    public PreciseOrderTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Look up the last known status Acme Retail recorded for a customer's order, by order ID.")
    public String getOrderStatus(
            @ToolParam(description = "The Acme Retail order ID, formatted exactly like ORD-1234 "
                    + "(the letters ORD, a hyphen, then four digits). Never a customer name or email address.")
            String orderId) {

        if (!ORDER_ID_PATTERN.matcher(orderId).matches()) {
            return "\"" + orderId + "\" is not a valid order ID. Order IDs are formatted like ORD-1234.";
        }

        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }
}
