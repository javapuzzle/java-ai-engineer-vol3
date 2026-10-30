package com.acme.retail.agents.ch15;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves {@link OrderAgent} is independently useful outside of any
 * orchestrator -- a specialist with narrow scope doesn't need to be
 * wired into a larger workflow to answer the one question it knows
 * how to answer.
 */
class OrderAgentTest {

    private final OrderAgent orderAgent = new OrderAgent(new OrderDirectory());

    @Test
    void answersASimpleOrderStatusRequestOnItsOwnWithNoOrchestratorInvolved() {
        String result = orderAgent.getOrderStatus("ORD-9042");

        assertThat(result).isEqualTo("Order ORD-9042 is currently PROCESSING.");
    }
}
