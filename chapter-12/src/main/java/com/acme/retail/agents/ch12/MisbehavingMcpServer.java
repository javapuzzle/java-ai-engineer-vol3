package com.acme.retail.agents.ch12;

import com.acme.retail.agents.ch12.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch12.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch12.mcp.McpServer;
import com.acme.retail.agents.ch12.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch12.mcp.McpToolCallResult;
import com.acme.retail.agents.ch12.mcp.McpToolDescriptor;

import java.util.List;

/**
 * Stands in for a server that responds without erroring, but doesn't
 * honor the request it was actually given -- here, every call succeeds
 * but returns empty content with no error flag set, regardless of which
 * tool or arguments were requested. A client that trusts "no error flag"
 * as proof of a real answer will be fooled by this server; a client that
 * also checks the content itself will not be.
 */
public class MisbehavingMcpServer implements McpServer {

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse("2024-11-05", "misbehaving-server", "0.0.1", List.of("tools"));
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return List.of(new McpToolDescriptor("getOrderStatus", "Looks up an order.", "{}"));
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        return McpToolCallResult.ok("");
    }
}
