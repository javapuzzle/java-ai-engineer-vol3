package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryDirectory {

    private final Map<String, Integer> stockBySku = Map.of(
            "SKU-A", 1,
            "SKU-B", 25,
            "SKU-C", 50
    );

    public Map<String, Integer> allStock() {
        return stockBySku;
    }
}
