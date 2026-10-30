package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * A specialist with exactly one job: flag SKUs running low on stock. It
 * has no idea whether any pending order actually needs that SKU -- that
 * connection, if it exists, is something only the orchestrator can see
 * by looking at more than one specialist's findings together.
 */
@Service
public class InventoryAnomalyMonitorAgent {

    private static final int LOW_STOCK_THRESHOLD = 2;

    private final InventoryDirectory inventoryDirectory;

    public InventoryAnomalyMonitorAgent(InventoryDirectory inventoryDirectory) {
        this.inventoryDirectory = inventoryDirectory;
    }

    public List<OpsFinding> scan() {
        return inventoryDirectory.allStock().entrySet().stream()
                .filter(entry -> entry.getValue() <= LOW_STOCK_THRESHOLD)
                .map(entry -> new OpsFinding(
                        "inventory-anomaly",
                        "SKU " + entry.getKey() + " has only " + entry.getValue() + " units left, at or below the low-stock threshold of " + LOW_STOCK_THRESHOLD + ".",
                        "MEDIUM",
                        entry.getKey()))
                .toList();
    }
}
