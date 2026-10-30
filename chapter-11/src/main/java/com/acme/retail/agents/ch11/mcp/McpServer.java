package com.acme.retail.agents.ch11.mcp;

import java.util.List;

/**
 * The three MCP operations every compliant client relies on, regardless
 * of which application built that client or which framework built the
 * server: initialize, discover, call. Any caller depending only on this
 * interface -- never on a concrete server class -- gets the portability
 * MCP exists to provide.
 */
public interface McpServer {

    McpInitializeResponse initialize(McpInitializeRequest request);

    List<McpToolDescriptor> listTools();

    McpToolCallResult callTool(McpToolCallRequest request);
}
