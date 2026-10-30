package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A specialist with exactly one job: flag orders stuck in processing
 * beyond a reasonable SLA. It knows nothing about inventory, and nothing
 * about what should happen once it flags something -- that decision
 * belongs entirely to the orchestrator.
 */
@Service
public class OrderBacklogMonitorAgent {

    private static final int SLA_MINUTES = 120;

    private final OrderDirectory orderDirectory;

    public OrderBacklogMonitorAgent(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    public List<OpsFinding> scan() {
        return orderDirectory.allOrders().stream()
                .filter(order -> "PROCESSING".equals(order.status()) && order.minutesInProcessing() > SLA_MINUTES)
                .map(order -> new OpsFinding(
                        "order-backlog",
                        "Order " + order.orderId() + " has been PROCESSING for " + order.minutesInProcessing() + " minutes, exceeding the " + SLA_MINUTES + "-minute SLA.",
                        "HIGH",
                        order.requiredSku()))
                .toList();
    }
}
