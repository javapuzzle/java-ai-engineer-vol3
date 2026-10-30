package com.acme.retail.agents.ch14.mcp;

/**
 * Stand-in for MCP's Resource descriptor -- read-only, addressable data,
 * distinct from a tool (an action) and a prompt (a reusable template).
 */
public record McpResourceDescriptor(String uri, String name, String description, String mimeType) {
}
