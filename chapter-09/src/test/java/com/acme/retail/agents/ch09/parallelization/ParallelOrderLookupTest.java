package com.acme.retail.agents.ch09.parallelization;

import com.acme.retail.agents.ch09.OrderDirectory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelOrderLookupTest {

    private final ParallelOrderLookup parallelOrderLookup = new ParallelOrderLookup(new OrderDirectory());

    @Test
    void combinesAllThreeIndependentLookupsIntoOneAnswer() {
        String result = parallelOrderLookup.lookupAll(List.of("ORD-7731", "ORD-9042", "ORD-1200"));

        assertThat(result)
                .as("all three real, independently-run lookups are represented in the combined result")
                .contains("Order ORD-7731 is currently SHIPPED.")
                .contains("Order ORD-9042 is currently PROCESSING.")
                .contains("Order ORD-1200 is currently CANCELLED.");
    }

    @Test
    void anUnknownOrderInTheBatchDoesNotPreventTheOthersFromResolving() {
        String result = parallelOrderLookup.lookupAll(List.of("ORD-7731", "ORD-0000"));

        assertThat(result)
                .contains("Order ORD-7731 is currently SHIPPED.")
                .contains("No order found with ID ORD-0000.");
    }
}
