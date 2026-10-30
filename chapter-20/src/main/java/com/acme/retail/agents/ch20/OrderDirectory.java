package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDirectory {

    private final List<OrderRecord> orders = List.of(
            new OrderRecord("ORD-7731", "PROCESSING", 150, "SKU-A"),
            new OrderRecord("ORD-9042", "PROCESSING", 20, "SKU-C"),
            new OrderRecord("ORD-1200", "SHIPPED", 0, "SKU-C")
    );

    public List<OrderRecord> allOrders() {
        return orders;
    }
}
