package com.acme.retail.agents.ch10;

/** One physical item within an order -- an order can contain more than one. */
public record OrderItem(String sku, String description) {
}
