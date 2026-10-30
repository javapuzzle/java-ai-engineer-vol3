package com.acme.retail.agents.ch03;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * The tool contract Sarah shipped first, and Maya caught live: a generic
 * description, an unnamed-in-spirit parameter with no {@code @ToolParam}
 * at all, and no validation of what arrives. This class is kept, on
 * purpose, as a permanent example of exactly what not to do -- see
 * {@link VagueOrderToolsTest} for what it actually produces.
 */
@Service
public class VagueOrderTools {

    private final OrderDirectory orderDirectory;

    public VagueOrderTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Checks an order.")
    public String check(String x) {
        return orderDirectory.findById(x)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + x + ".");
    }
}
