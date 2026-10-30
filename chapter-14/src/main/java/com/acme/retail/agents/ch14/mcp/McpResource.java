package com.acme.retail.agents.ch14.mcp;

/**
 * Stand-in for MCP's resources/read result -- the actual content behind
 * a resource URI, fetched on demand rather than pushed by the server.
 */
public record McpResource(String uri, String mimeType, String content) {
}
