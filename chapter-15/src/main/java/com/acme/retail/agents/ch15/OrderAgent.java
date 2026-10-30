package com.acme.retail.agents.ch15;

import org.springframework.stereotype.Service;

/**
 * A specialist agent with exactly one job: report an order's status.
 * It knows nothing about inventory or returns, and never needs to --
 * that narrowness is what makes it easy to reason about and easy to
 * reuse from more than one orchestrator.
 */
@Service
public class OrderAgent {

    private final OrderDirectory orderDirectory;

    public OrderAgent(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    public String getOrderStatus(String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }
}
