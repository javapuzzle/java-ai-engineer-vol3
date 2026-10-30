package com.acme.retail.agents.ch09.chaining;

import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A deliberately small, honest simplification -- the same kind Volume 2
 * Chapter 15 used for cross-lingual retrieval: a real system would use a
 * genuinely multilingual model here, not a hand-placed phrase table. This
 * step exists to prove chaining's ordering requirement, not to be a real
 * translator.
 */
public class SpanishToEnglishTranslationStep implements UnaryOperator<String> {

    private static final Map<String, String> KNOWN_PHRASES = Map.of(
            "¿dónde está mi pedido?", "where is my order?"
    );

    @Override
    public String apply(String input) {
        return KNOWN_PHRASES.getOrDefault(input.toLowerCase(Locale.ROOT), input);
    }
}
