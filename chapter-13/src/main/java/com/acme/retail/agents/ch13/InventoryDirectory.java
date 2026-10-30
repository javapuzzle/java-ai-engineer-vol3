package com.acme.retail.agents.ch13;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class InventoryDirectory {

    private final Map<String, Integer> stockBySku = Map.of(
            "SKU-A", 42,
            "SKU-B", 0,
            "SKU-C", 17
    );

    public Optional<Integer> findStockBySku(String sku) {
        return Optional.ofNullable(stockBySku.get(sku));
    }
}
