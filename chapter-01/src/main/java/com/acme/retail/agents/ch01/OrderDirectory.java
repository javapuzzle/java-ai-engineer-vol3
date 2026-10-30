package com.acme.retail.agents.ch01;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Stands in for the orders table -- a real, queryable record of what
 * Acme Retail's own system last recorded for an order. A real version is
 * backed by Postgres; this chapter only needs enough to prove the point.
 */
@Service
public class OrderDirectory {

    private final Map<String, OrderRecord> ordersById = Map.of(
            "ORD-7731", new OrderRecord("ORD-7731", "jane@example.com", "SHIPPED"),
            "ORD-9042", new OrderRecord("ORD-9042", "raj@example.com", "PROCESSING")
    );

    public Optional<OrderRecord> findById(String orderId) {
        return Optional.ofNullable(ordersById.get(orderId));
    }
}
