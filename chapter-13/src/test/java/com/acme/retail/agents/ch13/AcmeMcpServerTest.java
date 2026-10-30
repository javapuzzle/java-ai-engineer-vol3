package com.acme.retail.agents.ch13;

import com.acme.retail.agents.ch13.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch13.mcp.McpToolCallResult;
import com.acme.retail.agents.ch13.mcp.McpToolDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AcmeMcpServerTest {

    private final AcmeMcpServer server = new AcmeMcpServer(
            new AcmeOrderTools(new OrderDirectory()),
            new AcmeInventoryTools(new InventoryDirectory()));

    @Test
    void listToolsDescribesBothRealToolsWithTheirRealSpringAiMetadata() {
        List<McpToolDescriptor> tools = server.listTools();

        assertThat(tools).extracting(McpToolDescriptor::name)
                .containsExactlyInAnyOrder("getOrderStatus", "checkStock");
        assertThat(tools).allMatch(tool -> tool.version().equals("v1"));
        assertThat(tools).allMatch(tool -> tool.inputSchemaJson().contains("\"type\"") && tool.inputSchemaJson().contains("\"object\""));
    }

    @Test
    void callToolDispatchesToTheRealOrderStatusToolCallback() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void callToolDispatchesToTheRealInventoryToolCallback() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("checkStock", Map.of("sku", "SKU-C")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("17 units of SKU-C in stock.");
    }

    @Test
    void callToolWithAnUnknownToolNameReturnsAnErrorResult() {
        McpToolCallResult result = server.callTool(
                new McpToolCallRequest("deleteOrder", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isTrue();
        assertThat(result.content()).contains("Unknown tool");
    }
}
