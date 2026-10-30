package com.acme.retail.agents.ch14.mcp;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.support.ToolCallbacks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Bridges real Spring AI {@link ToolCallback}s -- the same mechanism
 * Chapter 2 used directly -- into the hand-written MCP protocol stand-ins
 * from Chapter 11. This is the one place in this chapter where "existing
 * @Tool-annotated methods" and "the MCP protocol shape" actually meet:
 * everything about the tools themselves stays real Spring AI; everything
 * about the protocol envelope stays the documented stand-in.
 */
public final class SpringAiToolToMcpAdapter {

    private final ToolCallback[] callbacks;

    public SpringAiToolToMcpAdapter(Object... toolObjects) {
        this.callbacks = ToolCallbacks.from(toolObjects);
    }

    public List<McpToolDescriptor> describeTools(String version) {
        return Arrays.stream(callbacks)
                .map(callback -> new McpToolDescriptor(
                        callback.getToolDefinition().name(),
                        callback.getToolDefinition().description(),
                        callback.getToolDefinition().inputSchema(),
                        version))
                .toList();
    }

    public Optional<ToolCallback> findByName(String toolName) {
        return Arrays.stream(callbacks)
                .filter(callback -> callback.getToolDefinition().name().equals(toolName))
                .findFirst();
    }
}
