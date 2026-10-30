package com.acme.retail.agents.ch18;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * A second, independent kind of hard limit: which actions a given agent
 * role is allowed to attempt at all, regardless of amount. An agent
 * whose job is answering order-status questions has no legitimate
 * reason to ever attempt {@code issueRefund} -- this guardrail makes
 * that impossible in code, rather than relying on the agent never
 * deciding to try.
 */
@Service
public class AllowedActionsGuardrail {

    private final Map<String, Set<String>> allowedActionsByAgentRole = Map.of(
            "order-status-agent", Set.of("getOrderStatus"),
            "refund-agent", Set.of("issueRefund", "getOrderStatus")
    );

    public boolean isActionAllowed(String agentRole, String actionName) {
        return allowedActionsByAgentRole.getOrDefault(agentRole, Set.of()).contains(actionName);
    }
}
