package com.acme.retail.agents.ch17;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OrderDirectory {

    private final Map<String, OrderRecord> ordersById = Map.of(
            "ORD-7731", new OrderRecord("ORD-7731", "SHIPPED", "WEST"),
            "ORD-9042", new OrderRecord("ORD-9042", "PROCESSING", "EAST")
    );

    public Optional<OrderRecord> findById(String orderId) {
        return Optional.ofNullable(ordersById.get(orderId));
    }
}
