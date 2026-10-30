package com.acme.retail.agents.ch14;

import com.acme.retail.agents.ch14.mcp.HttpSseTransport;
import com.acme.retail.agents.ch14.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch14.mcp.McpToolCallResult;
import com.acme.retail.agents.ch14.mcp.StdioTransport;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TransportTest {

    private final AcmeMcpServer server = new AcmeMcpServer(
            new AcmeOrderTools(new OrderDirectory()),
            new AcmeInventoryTools(new InventoryDirectory()),
            new ReturnPolicyDocument());

    @Test
    void stdioTransportCallsTheServerDirectlyWithNoSerializationInvolved() {
        StdioTransport transport = new StdioTransport();

        McpToolCallResult result = transport.callTool(server,
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void httpSseTransportProducesTheSameLogicalResultAfterARealJsonRoundTrip() {
        HttpSseTransport transport = new HttpSseTransport();

        McpToolCallResult result = transport.callTool(server,
                new McpToolCallRequest("getOrderStatus", Map.of("orderId", "ORD-7731")));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void bothTransportsAgreeOnTheAnswerForTheSameRequest() {
        McpToolCallRequest request = new McpToolCallRequest("checkStock", Map.of("sku", "SKU-C"));

        McpToolCallResult stdioResult = new StdioTransport().callTool(server, request);
        McpToolCallResult httpResult = new HttpSseTransport().callTool(server, request);

        assertThat(httpResult.content()).isEqualTo(stdioResult.content());
        assertThat(httpResult.isError()).isEqualTo(stdioResult.isError());
    }
}
