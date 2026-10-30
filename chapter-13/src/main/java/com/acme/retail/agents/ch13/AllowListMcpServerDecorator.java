package com.acme.retail.agents.ch13;

import com.acme.retail.agents.ch13.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch13.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch13.mcp.McpServer;
import com.acme.retail.agents.ch13.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch13.mcp.McpToolCallResult;
import com.acme.retail.agents.ch13.mcp.McpToolDescriptor;

import java.util.List;
import java.util.Set;

/**
 * Wraps a real {@link McpServer} with a per-caller allow-list -- the
 * access-control concern that only starts to matter once a server is
 * reachable by callers outside Acme's own codebase. The decorator never
 * touches the delegate for a disallowed tool name, so a rejected call
 * costs nothing beyond the allow-list check itself.
 */
public class AllowListMcpServerDecorator implements McpServer {

    private final McpServer delegate;
    private final Set<String> allowedToolNames;

    public AllowListMcpServerDecorator(McpServer delegate, Set<String> allowedToolNames) {
        this.delegate = delegate;
        this.allowedToolNames = allowedToolNames;
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return delegate.initialize(request);
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return delegate.listTools().stream()
                .filter(tool -> allowedToolNames.contains(tool.name()))
                .toList();
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        if (!allowedToolNames.contains(request.toolName())) {
            return McpToolCallResult.error("Tool not permitted for this client: " + request.toolName());
        }
        return delegate.callTool(request);
    }
}
