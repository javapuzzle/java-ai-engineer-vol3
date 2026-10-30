package com.acme.retail.agents.ch14.mcp;

/**
 * Hand-written stand-in for the MCP specification's Tool descriptor shape
 * (name, description, JSON Schema for its inputs) -- no MCP SDK artifact
 * is resolvable in this offline build, so this models the real message
 * shape directly rather than depending on the real SDK type. {@code
 * version} is this chapter's addition: once a tool is published outside
 * Acme's own codebase, external clients can depend on its exact contract,
 * so changing that contract needs a visible version, not a silent edit.
 */
public record McpToolDescriptor(String name, String description, String inputSchemaJson, String version) {
}
