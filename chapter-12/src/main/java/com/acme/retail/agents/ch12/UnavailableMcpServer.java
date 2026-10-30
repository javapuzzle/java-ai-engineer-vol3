package com.acme.retail.agents.ch12;

import com.acme.retail.agents.ch12.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch12.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch12.mcp.McpServer;
import com.acme.retail.agents.ch12.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch12.mcp.McpToolCallResult;
import com.acme.retail.agents.ch12.mcp.McpToolDescriptor;

import java.util.List;

/**
 * Stands in for a partner server that is down, unreachable, or timing
 * out -- every method throws, exactly as a real network call would fail
 * if the connection itself is the problem, not any individual tool.
 */
public class UnavailableMcpServer implements McpServer {

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        throw new IllegalStateException("Connection refused: server is unavailable.");
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        throw new IllegalStateException("Connection refused: server is unavailable.");
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        throw new IllegalStateException("Connection refused: server is unavailable.");
    }
}
