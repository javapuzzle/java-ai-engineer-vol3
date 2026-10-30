package com.acme.retail.agents.ch17;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A shared mutable workspace multiple agents contribute to without
 * holding a reference to each other at all -- collaboration through
 * shared state instead of a direct call. Neither agent needs to know
 * the other exists; each only needs to know the blackboard's fact
 * names.
 */
public class Blackboard {

    private final Map<String, Object> facts = new HashMap<>();

    public void put(String factName, Object value) {
        facts.put(factName, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String factName, Class<T> type) {
        return Optional.ofNullable(facts.get(factName)).map(type::cast);
    }

    public boolean has(String factName) {
        return facts.containsKey(factName);
    }
}
