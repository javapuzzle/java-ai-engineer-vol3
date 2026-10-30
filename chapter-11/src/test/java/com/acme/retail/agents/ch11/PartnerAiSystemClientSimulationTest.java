package com.acme.retail.agents.ch11;

import com.acme.retail.agents.ch11.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch11.mcp.McpServer;
import com.acme.retail.agents.ch11.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch11.mcp.McpToolCallResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simulates a partner's own AI system connecting to Acme's MCP server as
 * an entirely independent caller. This test depends only on the
 * McpServer interface -- never on AcmeOrderMcpServer directly -- exactly
 * as a real external client, written by a team that has never seen
 * Acme's source code, would be forced to.
 */
class PartnerAiSystemClientSimulationTest {

    @Test
    void aPartnerSystemCanDriveAcmesServerKnowingOnlyTheMcpContract() {
        McpServer server = new AcmeOrderMcpServer(new OrderDirectory());

        server.initialize(new McpInitializeRequest("partner-logistics-ai", "3.2.0"));
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-9042")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-9042 is currently PROCESSING.");
    }
}
