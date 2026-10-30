package com.acme.retail.agents.ch09.chaining;

import java.util.function.UnaryOperator;

/**
 * The step that only works correctly if translation already happened --
 * it looks for the English phrase "where is my order," and has no idea
 * Spanish exists. Chaining's whole discipline is getting steps like this
 * one in the right order.
 */
public class ClassificationStep implements UnaryOperator<String> {

    @Override
    public String apply(String input) {
        String normalized = input.toLowerCase(java.util.Locale.ROOT);
        return normalized.contains("where is my order")
                ? "[intent=order-status] " + input
                : "[intent=unknown] " + input;
    }
}
