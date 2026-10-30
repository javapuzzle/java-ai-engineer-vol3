package com.acme.retail.agents.ch02;

/** Carried over from Chapter 1 -- the record a tool call ultimately resolves against. */
public record OrderRecord(String orderId, String customerEmail, String status) {
}
