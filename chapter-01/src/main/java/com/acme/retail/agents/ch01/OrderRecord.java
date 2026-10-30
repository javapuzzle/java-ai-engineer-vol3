package com.acme.retail.agents.ch01;

/**
 * A real, known order -- carried over conceptually from Volume 1's
 * {@code ProductFact}/Volume 2's structured knowledge base pattern. This
 * chapter's whole point is that even a real, valid order ID changes
 * nothing: there is still no code path capable of checking its live
 * status.
 */
public record OrderRecord(String orderId, String customerEmail, String status) {
}
