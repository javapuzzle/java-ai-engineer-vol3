package com.acme.retail.agents.ch17;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A specialist agent that knows about warehouses and stock -- and
 * nothing about orders or customers. {@link OrderAgent} delegates to
 * this agent directly, mid-task, because it needs an answer this agent
 * owns and it cannot produce on its own.
 */
@Service
public class WarehouseInventoryAgent {

    private final Map<String, Map<String, Integer>> stockByRegionAndSku = Map.of(
            "WEST", Map.of("SKU-A", 12, "SKU-B", 0),
            "EAST", Map.of("SKU-A", 3, "SKU-B", 8)
    );

    private final Map<String, String> nearestWarehouseByRegion = Map.of(
            "WEST", "Warehouse-West-1",
            "EAST", "Warehouse-East-1"
    );

    public WarehouseStockResult checkStockAtNearestWarehouse(String sku, String customerRegion) {
        String warehouse = nearestWarehouseByRegion.getOrDefault(customerRegion, "Warehouse-Central");
        int quantity = stockByRegionAndSku.getOrDefault(customerRegion, Map.of()).getOrDefault(sku, 0);
        return new WarehouseStockResult(warehouse, quantity);
    }

    /**
     * Blackboard collaboration: this agent reads a fact ({@code
     * customerRegion}) that {@link OrderAgent} posted, and writes its
     * own finding back -- without ever holding a reference to
     * OrderAgent itself. Both agents only ever know the blackboard's
     * fact names, not each other.
     */
    public void postStockFinding(Blackboard blackboard, String sku) {
        String customerRegion = blackboard.get("customerRegion", String.class)
                .orElseThrow(() -> new IllegalStateException("No customerRegion fact on the blackboard yet."));
        blackboard.put("stockResult", checkStockAtNearestWarehouse(sku, customerRegion));
    }
}
