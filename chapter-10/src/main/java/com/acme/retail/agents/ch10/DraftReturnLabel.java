package com.acme.retail.agents.ch10;

/**
 * A first-pass, uncommitted guess at which item a return request is
 * about -- exactly the naive shortcut that shipped the real bug this
 * chapter opens with: default to the first item on the order, without
 * checking it against what the customer actually described.
 */
public record DraftReturnLabel(String orderId, OrderItem chosenItem, String reasoning) {
}
