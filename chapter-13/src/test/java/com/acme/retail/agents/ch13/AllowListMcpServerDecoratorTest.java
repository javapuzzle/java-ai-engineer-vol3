package com.acme.retail.agents.ch13;

import com.acme.retail.agents.ch13.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch13.mcp.McpToolCallResult;
import com.acme.retail.agents.ch13.mcp.McpToolDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AllowListMcpServerDecoratorTest {

    private final AcmeMcpServer realServer = new AcmeMcpServer(
            new AcmeOrderTools(new OrderDirectory()),
            new AcmeInventoryTools(new InventoryDirectory()));

    @Test
    void anAllowedToolCallPassesThroughToTheRealServerUnchanged() {
        AllowListMcpServerDecorator restrictedServer =
                new AllowListMcpServerDecorator(realServer, Set.of("getOrderStatus"));

        McpToolCallResult result = restrictedServer.callTool(
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void aDisallowedToolCallIsRejectedWithoutReachingTheRealServer() {
        AllowListMcpServerDecorator restrictedServer =
                new AllowListMcpServerDecorator(realServer, Set.of("getOrderStatus"));

        McpToolCallResult result = restrictedServer.callTool(
                new McpToolCallRequest("checkStock", Map.of("sku", "SKU-C")));

        assertThat(result.isError()).isTrue();
        assertThat(result.content()).contains("not permitted");
    }

    @Test
    void listToolsOnlyShowsAllowedToolsToARestrictedClient() {
        AllowListMcpServerDecorator restrictedServer =
                new AllowListMcpServerDecorator(realServer, Set.of("getOrderStatus"));

        List<McpToolDescriptor> tools = restrictedServer.listTools();

        assertThat(tools).extracting(McpToolDescriptor::name).containsExactly("getOrderStatus");
    }
}
