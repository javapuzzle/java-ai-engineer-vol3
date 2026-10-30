package com.acme.retail.agents.ch15;

import org.springframework.stereotype.Service;

/**
 * A specialist agent with exactly one job: report stock for a SKU. It
 * has no idea what an order or a return is -- the orchestrator is the
 * only thing that knows how this specialist's answer relates to any
 * other specialist's.
 */
@Service
public class InventoryAgent {

    private final InventoryDirectory inventoryDirectory;

    public InventoryAgent(InventoryDirectory inventoryDirectory) {
        this.inventoryDirectory = inventoryDirectory;
    }

    public String checkStock(String sku) {
        return inventoryDirectory.findStockBySku(sku)
                .map(quantity -> quantity > 0
                        ? quantity + " units of " + sku + " in stock."
                        : sku + " is currently out of stock.")
                .orElse("No SKU found: " + sku + ".");
    }

    public boolean isInStock(String sku) {
        return inventoryDirectory.findStockBySku(sku).map(quantity -> quantity > 0).orElse(false);
    }
}
