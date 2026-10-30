package com.acme.retail.agents.ch12;

import com.acme.retail.agents.ch12.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch12.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch12.mcp.McpServer;
import com.acme.retail.agents.ch12.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch12.mcp.McpToolCallResult;
import com.acme.retail.agents.ch12.mcp.McpToolDescriptor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A working MCP server that Sarah does not own or control -- standing in
 * for a partner logistics company's real, healthy tracking server. This
 * class exists only to give {@link McpClient} something real to connect
 * to; Sarah's client code depends solely on the {@link McpServer}
 * interface, never on this implementation.
 */
@Service
public class PartnerOrderMcpServer implements McpServer {

    private static final String GET_ORDER_STATUS = "getOrderStatus";

    private final OrderDirectory orderDirectory;

    public PartnerOrderMcpServer(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse(
                "2024-11-05",
                "partner-logistics-mcp-server",
                "2.4.0",
                List.of("tools"));
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return List.of(new McpToolDescriptor(
                GET_ORDER_STATUS,
                "Looks up the current tracking status of an order by its order ID.",
                "{\"type\":\"object\",\"properties\":{\"orderId\":{\"type\":\"string\"}},\"required\":[\"orderId\"]}"));
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        if (!GET_ORDER_STATUS.equals(request.toolName())) {
            return McpToolCallResult.error("Unknown tool: " + request.toolName());
        }
        String orderId = request.arguments().get("orderId");
        return orderDirectory.findById(orderId)
                .map(order -> McpToolCallResult.ok("Order " + order.orderId() + " is currently " + order.status() + "."))
                .orElseGet(() -> McpToolCallResult.error("No order found with ID " + orderId + "."));
    }
}
