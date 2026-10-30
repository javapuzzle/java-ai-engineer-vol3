package com.acme.retail.agents.ch11;

import com.acme.retail.agents.ch11.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch11.mcp.McpServer;
import com.acme.retail.agents.ch11.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch11.mcp.McpToolCallResult;
import com.acme.retail.agents.ch11.mcp.McpToolDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simulates a completely different internal Acme dashboard connecting to
 * the exact same server instance used by {@link PartnerAiSystemClientSimulationTest}.
 * Neither test, nor the server itself, changes to accommodate the other
 * caller -- that is the interoperability MCP is built to provide.
 */
class InternalDashboardClientSimulationTest {

    @Test
    void anUnrelatedInternalDashboardCanDriveTheSameServerTheSameWay() {
        McpServer server = new AcmeOrderMcpServer(new OrderDirectory());

        server.initialize(new McpInitializeRequest("acme-ops-dashboard", "0.9.1"));
        List<McpToolDescriptor> tools = server.listTools();
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest(tools.get(0).name(), Map.of("orderId", "ORD-1200")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-1200 is currently CANCELLED.");
    }
}
