package com.acme.retail.agents.ch12;

import com.acme.retail.agents.ch12.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch12.mcp.McpServer;
import com.acme.retail.agents.ch12.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch12.mcp.McpToolCallResult;
import com.acme.retail.agents.ch12.mcp.McpToolDescriptor;

import java.util.List;

/**
 * Wraps a {@link McpServer} the caller does not own or control. Every
 * public method here depends only on the McpServer contract -- never on
 * which concrete implementation is behind it -- and converts every
 * failure mode (a thrown exception, or a response this client decides
 * not to trust) into a {@link McpClientResult} instead of letting either
 * one propagate to Sarah's own application code.
 */
public class McpClient {

    private final McpServer server;
    private boolean connected = false;

    public McpClient(McpServer server) {
        this.server = server;
    }

    public McpClientResult connect() {
        try {
            server.initialize(new McpInitializeRequest("acme-assistant", "1.0"));
            connected = true;
            return McpClientResult.success("connected");
        } catch (RuntimeException e) {
            connected = false;
            return McpClientResult.failure("Failed to connect: " + e.getMessage());
        }
    }

    public List<McpToolDescriptor> discoverTools() {
        if (!connected) {
            return List.of();
        }
        try {
            return server.listTools();
        } catch (RuntimeException e) {
            return List.of();
        }
    }

    public McpClientResult callTool(McpToolCallRequest request) {
        if (!connected) {
            return McpClientResult.failure("Not connected: call connect() first.");
        }
        McpToolCallResult result;
        try {
            result = server.callTool(request);
        } catch (RuntimeException e) {
            return McpClientResult.failure("Tool call failed: " + e.getMessage());
        }
        if (result.isError()) {
            return McpClientResult.failure(result.content());
        }
        if (result.content() == null || result.content().isBlank()) {
            return McpClientResult.failure("Server returned an empty result for tool: " + request.toolName());
        }
        return McpClientResult.success(result.content());
    }
}
