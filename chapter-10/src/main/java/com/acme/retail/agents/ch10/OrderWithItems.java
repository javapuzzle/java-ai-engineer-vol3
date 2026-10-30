package com.acme.retail.agents.ch10;

import java.util.List;

public record OrderWithItems(String orderId, List<OrderItem> items) {
}
