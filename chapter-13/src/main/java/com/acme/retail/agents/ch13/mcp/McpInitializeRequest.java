package com.acme.retail.agents.ch13.mcp;

/**
 * Stand-in for MCP's initialize request -- the first message any client
 * sends before it may list or call tools, identifying itself to the
 * server.
 */
public record McpInitializeRequest(String clientName, String clientVersion) {
}
