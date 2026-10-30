package com.acme.retail.agents.ch14.mcp;

/**
 * How a client's tool call actually reaches a server -- an operational
 * decision, not just a protocol detail. Both implementations produce
 * logically identical results for the same request; they differ only in
 * what happens on the wire between the call and the result.
 */
public interface McpTransport {

    McpToolCallResult callTool(McpServer server, McpToolCallRequest request);
}
