package com.acme.retail.agents.ch19;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderDirectory {

    private final Map<String, OrderRecord> ordersById = Map.of(
            "ORD-7731", new OrderRecord("ORD-7731", new BigDecimal("150.00")),
            "ORD-9042", new OrderRecord("ORD-9042", new BigDecimal("500.00"))
    );

    public Optional<OrderRecord> findById(String orderId) {
        return Optional.ofNullable(ordersById.get(orderId));
    }
}
