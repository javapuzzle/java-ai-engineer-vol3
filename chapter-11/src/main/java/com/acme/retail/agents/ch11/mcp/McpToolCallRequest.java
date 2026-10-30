package com.acme.retail.agents.ch11.mcp;

import java.util.Map;

/**
 * Stand-in for MCP's tools/call request -- a tool name plus its
 * arguments, exactly as any compliant client would send it regardless of
 * which framework built that client.
 */
public record McpToolCallRequest(String toolName, Map<String, String> arguments) {
}
