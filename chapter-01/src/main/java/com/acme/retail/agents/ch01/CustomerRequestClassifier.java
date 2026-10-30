package com.acme.retail.agents.ch01;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Classifies a customer request by what it would take to resolve it --
 * not by how confident an LLM sounds when it tries.
 *
 * <p>The phrase list below is deliberately small and honest: it only
 * recognizes requests that need a live action performed somewhere else
 * (an order-tracking system, a returns system). Everything else --
 * anything Volumes 1-2's structured lookup or retrieval could already
 * settle -- falls through to {@link RequestType#DESCRIBABLE}.
 */
@Service
public class CustomerRequestClassifier {

    private static final List<String> ACTION_PHRASES = List.of(
            "where is my order", "track my order", "cancel my order",
            "return this", "check inventory at"
    );

    public RequestType classify(String request) {
        String normalized = request.toLowerCase(Locale.ROOT);
        boolean matchesActionPhrase = ACTION_PHRASES.stream().anyMatch(normalized::contains);

        return matchesActionPhrase ? RequestType.ACTIONABLE : RequestType.DESCRIBABLE;
    }
}
