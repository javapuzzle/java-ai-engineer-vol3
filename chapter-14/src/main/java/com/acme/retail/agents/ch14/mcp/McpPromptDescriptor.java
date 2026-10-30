package com.acme.retail.agents.ch14.mcp;

import java.util.List;

/**
 * Stand-in for MCP's Prompt descriptor -- a named, reusable, versioned
 * template, distinct from a tool (an action) and a resource (static
 * data). {@code argumentNames} lists the placeholders the template
 * expects to be filled in at render time.
 */
public record McpPromptDescriptor(String name, String description, List<String> argumentNames) {
}
