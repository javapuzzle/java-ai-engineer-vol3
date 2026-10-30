package com.acme.retail.agents.ch20.mcp;

/**
 * Stand-in for MCP's tools/call result -- content plus an explicit error
 * flag, so a caller never has to guess whether a returned string is a
 * real answer or a failure message.
 */
public record McpToolCallResult(String content, boolean isError) {

    public static McpToolCallResult ok(String content) {
        return new McpToolCallResult(content, false);
    }

    public static McpToolCallResult error(String message) {
        return new McpToolCallResult(message, true);
    }
}
