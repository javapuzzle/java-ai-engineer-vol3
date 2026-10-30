package com.acme.retail.agents.ch13;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * The same kind of real Spring AI @Tool-annotated class introduced in
 * Chapter 2 -- nothing about it changes to become MCP-servable.
 * SpringAiToolToMcpAdapter is what does the bridging, not this class.
 */
@Service
public class AcmeOrderTools {

    private final OrderDirectory orderDirectory;

    public AcmeOrderTools(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Tool(description = "Looks up the current status of an Acme Retail order by its order ID.")
    public String getOrderStatus(@ToolParam(description = "The order ID, formatted like ORD-1234") String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }
}
