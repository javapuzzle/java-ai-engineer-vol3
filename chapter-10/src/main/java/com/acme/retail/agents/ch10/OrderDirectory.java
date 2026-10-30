package com.acme.retail.agents.ch10;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderDirectory {

    private final Map<String, OrderWithItems> ordersById = Map.of(
            "ORD-7731", new OrderWithItems("ORD-7731", List.of(
                    new OrderItem("SKU-A", "Blue Blender"),
                    new OrderItem("SKU-B", "Red Blender")
            )),
            "ORD-9042", new OrderWithItems("ORD-9042", List.of(
                    new OrderItem("SKU-C", "Wireless Headphones")
            ))
    );

    public Optional<OrderWithItems> findById(String orderId) {
        return Optional.ofNullable(ordersById.get(orderId));
    }
}
