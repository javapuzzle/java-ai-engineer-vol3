package com.acme.retail.agents.ch04;

/** Which category an order's product belongs to, and its last known status. */
public record OrderRecord(String orderId, String status, String category) {
}
