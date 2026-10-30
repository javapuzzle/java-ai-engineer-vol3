package com.acme.retail.agents.ch20.mcp;

/**
 * Hand-written stand-in for the MCP specification's Tool descriptor shape
 * (name, description, JSON Schema for its inputs) -- no MCP SDK artifact
 * is resolvable in this offline build, so this models the real message
 * shape directly rather than depending on the real SDK type.
 */
public record McpToolDescriptor(String name, String description, String inputSchemaJson) {
}
