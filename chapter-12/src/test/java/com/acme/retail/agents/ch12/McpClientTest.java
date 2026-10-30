package com.acme.retail.agents.ch12;

import com.acme.retail.agents.ch12.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch12.mcp.McpToolDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class McpClientTest {

    @Test
    void connectingToAWorkingServerAllowsDiscoveryAndSuccessfulToolCalls() {
        McpClient client = new McpClient(new PartnerOrderMcpServer(new OrderDirectory()));

        McpClientResult connectResult = client.connect();
        List<McpToolDescriptor> tools = client.discoverTools();
        McpClientResult callResult = client.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(connectResult.success()).isTrue();
        assertThat(tools).hasSize(1);
        assertThat(callResult.success()).isTrue();
        assertThat(callResult.value()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void connectingToAnUnavailableServerFailsGracefullyInsteadOfThrowing() {
        McpClient client = new McpClient(new UnavailableMcpServer());

        McpClientResult connectResult = client.connect();
        List<McpToolDescriptor> tools = client.discoverTools();

        assertThat(connectResult.success()).isFalse();
        assertThat(connectResult.errorMessage()).contains("Failed to connect");
        assertThat(tools).isEmpty();
    }

    @Test
    void callingAToolWithoutConnectingFirstFailsWithoutTouchingTheServer() {
        McpClient client = new McpClient(new UnavailableMcpServer());

        McpClientResult result = client.callTool(new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.success()).isFalse();
        assertThat(result.errorMessage()).contains("Not connected");
    }

    @Test
    void aMisbehavingServersEmptyResultIsTreatedAsAFailureNotASuccess() {
        McpClient client = new McpClient(new MisbehavingMcpServer());
        client.connect();

        McpClientResult result = client.callTool(new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.success()).isFalse();
        assertThat(result.errorMessage()).contains("empty result");
    }
}
