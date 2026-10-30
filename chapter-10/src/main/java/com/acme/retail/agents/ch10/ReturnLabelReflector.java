package com.acme.retail.agents.ch10;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Checks a draft against the actual customer request before letting it
 * commit -- the full implementation of the idea Volume 2 Chapter 12
 * introduced by scenario only ("judge whether retrieval was good enough
 * before trusting it") and explicitly deferred. Here the same judgment
 * applies to an action's draft, not a retrieved chunk: does the evidence
 * (the customer's own words) actually support what's about to happen?
 */
@Service
public class ReturnLabelReflector {

    private static final List<String> KNOWN_COLOR_WORDS = List.of("blue", "red", "green", "black", "white");

    public ReturnLabelResult reflect(String customerRequest, DraftReturnLabel draft, OrderWithItems order) {
        String normalizedRequest = customerRequest.toLowerCase(Locale.ROOT);
        Optional<String> mentionedColor = KNOWN_COLOR_WORDS.stream()
                .filter(normalizedRequest::contains)
                .findFirst();

        if (mentionedColor.isEmpty()) {
            return ReturnLabelResult.confirmed(draft.chosenItem(),
                    "The request doesn't specify which item; the draft's default is the best available guess.");
        }

        if (describesColor(draft.chosenItem(), mentionedColor.get())) {
            return ReturnLabelResult.confirmed(draft.chosenItem(),
                    "Draft item's color is supported by the customer's request.");
        }

        Optional<OrderItem> betterMatch = order.items().stream()
                .filter(item -> describesColor(item, mentionedColor.get()))
                .findFirst();

        if (betterMatch.isPresent()) {
            return ReturnLabelResult.revised(betterMatch.get(),
                    "Draft item's color did not match the customer's request; revised to "
                            + betterMatch.get().description() + ", which does.");
        }

        return ReturnLabelResult.needsReview(
                "The customer mentioned \"" + mentionedColor.get() + "\", which doesn't match any item on this order.");
    }

    private boolean describesColor(OrderItem item, String color) {
        return item.description().toLowerCase(Locale.ROOT).contains(color);
    }
}
