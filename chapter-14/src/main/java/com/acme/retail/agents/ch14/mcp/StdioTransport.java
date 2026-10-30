package com.acme.retail.agents.ch14.mcp;

/**
 * Stdio transport: the client and server are two processes talking over
 * a direct pipe, typically on the same machine. There is no network hop
 * and no serialization boundary this code needs to model beyond the
 * in-process method call itself -- which is exactly what makes stdio
 * simple to operate, and exactly why it can't be used to reach a server
 * running on a different machine.
 */
public class StdioTransport implements McpTransport {

    @Override
    public McpToolCallResult callTool(McpServer server, McpToolCallRequest request) {
        return server.callTool(request);
    }
}
