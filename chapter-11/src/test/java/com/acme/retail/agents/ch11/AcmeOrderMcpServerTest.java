package com.acme.retail.agents.ch11;

import com.acme.retail.agents.ch11.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch11.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch11.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch11.mcp.McpToolCallResult;
import com.acme.retail.agents.ch11.mcp.McpToolDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AcmeOrderMcpServerTest {

    private final AcmeOrderMcpServer server = new AcmeOrderMcpServer(new OrderDirectory());

    @Test
    void initializeAnnouncesTheProtocolVersionAndTheToolsCapability() {
        McpInitializeResponse response = server.initialize(new McpInitializeRequest("test-client", "1.0"));

        assertThat(response.protocolVersion()).isEqualTo("2024-11-05");
        assertThat(response.capabilities()).contains("tools");
    }

    @Test
    void listToolsDescribesTheGetOrderStatusToolWithARealJsonSchema() {
        List<McpToolDescriptor> tools = server.listTools();

        assertThat(tools).hasSize(1);
        assertThat(tools.get(0).name()).isEqualTo("getOrderStatus");
        assertThat(tools.get(0).inputSchemaJson()).contains("\"orderId\"");
    }

    @Test
    void callToolWithAKnownOrderIdReturnsItsStatus() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void callToolWithAnUnknownOrderIdReturnsAnErrorResult() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-0000")));

        assertThat(result.isError()).isTrue();
    }

    @Test
    void callToolWithAnUnknownToolNameReturnsAnErrorResult() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("deleteOrder", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isTrue();
        assertThat(result.content()).contains("Unknown tool");
    }
}
