package com.acme.retail.agents.ch12.mcp;

import java.util.List;

/**
 * Stand-in for MCP's initialize response -- the server's identity, the
 * protocol version it speaks, and which capabilities (tools, resources,
 * prompts) it actually supports. "2024-11-05" mirrors the real MCP
 * specification's protocol version string at the time this volume was
 * written.
 */
public record McpInitializeResponse(
        String protocolVersion,
        String serverName,
        String serverVersion,
        List<String> capabilities) {
}
