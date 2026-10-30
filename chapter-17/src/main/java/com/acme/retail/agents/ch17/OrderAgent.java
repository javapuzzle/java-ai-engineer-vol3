package com.acme.retail.agents.ch17;

import org.springframework.stereotype.Service;

/**
 * Delegation: this agent holds a direct reference to
 * {@link WarehouseInventoryAgent} and calls it mid-task, because
 * answering "is this in stock at the customer's nearest warehouse"
 * requires information only that agent owns. This is a direct
 * object-to-object call, not a round trip through an orchestrator --
 * the two agents are talking to each other, not just being sequenced by
 * a third party.
 */
@Service
public class OrderAgent {

    private final OrderDirectory orderDirectory;
    private final WarehouseInventoryAgent warehouseInventoryAgent;

    public OrderAgent(OrderDirectory orderDirectory, WarehouseInventoryAgent warehouseInventoryAgent) {
        this.orderDirectory = orderDirectory;
        this.warehouseInventoryAgent = warehouseInventoryAgent;
    }

    public String handleNearestWarehouseStockRequest(String orderId, String sku) {
        return orderDirectory.findById(orderId)
                .map(order -> {
                    WarehouseStockResult stock = warehouseInventoryAgent.checkStockAtNearestWarehouse(sku, order.customerRegion());
                    return stock.quantity() > 0
                            ? stock.quantity() + " units of " + sku + " available at " + stock.warehouse() + "."
                            : sku + " is not available at " + stock.warehouse() + ".";
                })
                .orElse("No order found with ID " + orderId + ".");
    }

    /**
     * Blackboard collaboration: this agent posts what it knows (the
     * customer's region) as a fact on the shared blackboard and never
     * calls {@link WarehouseInventoryAgent} directly -- it doesn't hold
     * a reference to it at all. Whatever reads the blackboard next
     * decides what to do with that fact.
     */
    public void postOrderContext(Blackboard blackboard, String orderId) {
        orderDirectory.findById(orderId).ifPresent(order -> {
            blackboard.put("orderId", orderId);
            blackboard.put("customerRegion", order.customerRegion());
        });
    }
}
