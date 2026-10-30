package com.acme.retail.agents.ch13;

import com.acme.retail.agents.ch13.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch13.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch13.mcp.McpServer;
import com.acme.retail.agents.ch13.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch13.mcp.McpToolCallResult;
import com.acme.retail.agents.ch13.mcp.McpToolDescriptor;
import com.acme.retail.agents.ch13.mcp.SpringAiToolToMcpAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Acme's real order and inventory tools, published as a single MCP
 * server. Every tool descriptor here comes from real Spring AI
 * ToolCallback metadata via {@link SpringAiToolToMcpAdapter} -- nothing
 * about the tool definitions themselves is hand-written for MCP; only
 * the protocol envelope around them is.
 */
@Service
public class AcmeMcpServer implements McpServer {

    private static final String CONTRACT_VERSION = "v1";

    private final SpringAiToolToMcpAdapter adapter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AcmeMcpServer(AcmeOrderTools orderTools, AcmeInventoryTools inventoryTools) {
        this.adapter = new SpringAiToolToMcpAdapter(orderTools, inventoryTools);
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse("2024-11-05", "acme-retail-mcp-server", "1.0.0", List.of("tools"));
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return adapter.describeTools(CONTRACT_VERSION);
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        return adapter.findByName(request.toolName())
                .map(callback -> invoke(callback, request))
                .orElseGet(() -> McpToolCallResult.error("Unknown tool: " + request.toolName()));
    }

    private McpToolCallResult invoke(ToolCallback callback, McpToolCallRequest request) {
        try {
            String argumentsJson = objectMapper.writeValueAsString(request.arguments());
            String rawResult = callback.call(argumentsJson);
            return McpToolCallResult.ok(unwrap(rawResult));
        } catch (Exception e) {
            return McpToolCallResult.error("Tool invocation failed: " + e.getMessage());
        }
    }

    /**
     * Spring AI's internal JSON conversion only quotes a returned string
     * when it doesn't already look like a JSON value on its own -- a
     * result that happens to start with a digit (like "17 units...")
     * comes back completely unquoted, while ordinary text comes back
     * quoted. Only unwrap when a real JSON string literal is present;
     * otherwise trust the raw text as-is.
     */
    private String unwrap(String rawResult) throws com.fasterxml.jackson.core.JsonProcessingException {
        if (rawResult.startsWith("\"") && rawResult.endsWith("\"")) {
            return objectMapper.readValue(rawResult, String.class);
        }
        return rawResult;
    }
}
