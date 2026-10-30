package com.acme.retail.agents.ch13;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class AcmeInventoryTools {

    private final InventoryDirectory inventoryDirectory;

    public AcmeInventoryTools(InventoryDirectory inventoryDirectory) {
        this.inventoryDirectory = inventoryDirectory;
    }

    @Tool(description = "Checks the current stock quantity for a product by its SKU.")
    public String checkStock(@ToolParam(description = "The product SKU, formatted like SKU-A") String sku) {
        return inventoryDirectory.findStockBySku(sku)
                .map(quantity -> quantity + " units of " + sku + " in stock.")
                .orElse("No SKU found: " + sku + ".");
    }
}
