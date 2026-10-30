package com.acme.retail.agents.ch14.mcp;

import java.util.List;

/**
 * The additional MCP operations this chapter adds on top of the
 * tools-only {@link McpServer} contract from Chapter 11 -- listing and
 * reading resources, and listing and rendering prompts. A server can
 * implement this without changing anything about how it already
 * implements McpServer's tool operations.
 */
public interface McpResourceAndPromptServer extends McpServer {

    List<McpResourceDescriptor> listResources();

    McpResource readResource(String uri);

    List<McpPromptDescriptor> listPrompts();

    String getPrompt(String promptName, java.util.Map<String, String> arguments);
}
