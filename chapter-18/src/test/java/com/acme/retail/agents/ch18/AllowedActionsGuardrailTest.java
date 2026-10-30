package com.acme.retail.agents.ch18;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllowedActionsGuardrailTest {

    private final AllowedActionsGuardrail guardrail = new AllowedActionsGuardrail();

    @Test
    void anOrderStatusAgentIsAllowedToCheckOrderStatus() {
        assertThat(guardrail.isActionAllowed("order-status-agent", "getOrderStatus")).isTrue();
    }

    @Test
    void anOrderStatusAgentIsNeverAllowedToIssueARefund() {
        assertThat(guardrail.isActionAllowed("order-status-agent", "issueRefund")).isFalse();
    }

    @Test
    void anUnknownAgentRoleIsAllowedNoActionsAtAll() {
        assertThat(guardrail.isActionAllowed("unregistered-agent", "getOrderStatus")).isFalse();
    }
}
