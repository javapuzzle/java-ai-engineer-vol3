package com.acme.retail.agents.ch11;

import com.acme.retail.agents.ch11.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch11.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch11.mcp.McpServer;
import com.acme.retail.agents.ch11.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch11.mcp.McpToolCallResult;
import com.acme.retail.agents.ch11.mcp.McpToolDescriptor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Acme's order-status tool, published as an MCP server. Any compliant
 * client can initialize, discover, and call this exact same instance --
 * nothing about it is specific to Spring Boot, a particular framework,
 * or a particular caller, which is precisely the interoperability
 * problem ad hoc tool-calling was never designed to solve.
 */
@Service
public class AcmeOrderMcpServer implements McpServer {

    private static final String GET_ORDER_STATUS = "getOrderStatus";

    private final OrderDirectory orderDirectory;

    public AcmeOrderMcpServer(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse(
                "2024-11-05",
                "acme-order-mcp-server",
                "1.0.0",
                List.of("tools"));
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return List.of(new McpToolDescriptor(
                GET_ORDER_STATUS,
                "Looks up the current status of an Acme Retail order by its order ID.",
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
