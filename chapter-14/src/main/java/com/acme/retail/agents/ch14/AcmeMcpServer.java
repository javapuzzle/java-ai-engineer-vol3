package com.acme.retail.agents.ch14;

import com.acme.retail.agents.ch14.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch14.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch14.mcp.McpPromptDescriptor;
import com.acme.retail.agents.ch14.mcp.McpPromptTemplate;
import com.acme.retail.agents.ch14.mcp.McpResource;
import com.acme.retail.agents.ch14.mcp.McpResourceAndPromptServer;
import com.acme.retail.agents.ch14.mcp.McpResourceDescriptor;
import com.acme.retail.agents.ch14.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch14.mcp.McpToolCallResult;
import com.acme.retail.agents.ch14.mcp.McpToolDescriptor;
import com.acme.retail.agents.ch14.mcp.SpringAiToolToMcpAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Acme's real order and inventory tools, its return-policy resource, and
 * its "summarize this order" prompt template, all published as a single
 * MCP server. Every tool descriptor here comes from real Spring AI
 * ToolCallback metadata via {@link SpringAiToolToMcpAdapter} -- nothing
 * about the tool definitions themselves is hand-written for MCP; only
 * the protocol envelope around them, and the resource/prompt operations
 * MCP defines separately from tools, are.
 */
@Service
public class AcmeMcpServer implements McpResourceAndPromptServer {

    private static final String CONTRACT_VERSION = "v1";
    private static final McpPromptTemplate SUMMARIZE_ORDER_PROMPT = new McpPromptTemplate(
            "summarize_order",
            "Summarize the current status of order {orderId} for the customer in one sentence.");

    private final SpringAiToolToMcpAdapter adapter;
    private final ReturnPolicyDocument returnPolicyDocument;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AcmeMcpServer(AcmeOrderTools orderTools, AcmeInventoryTools inventoryTools,
                          ReturnPolicyDocument returnPolicyDocument) {
        this.adapter = new SpringAiToolToMcpAdapter(orderTools, inventoryTools);
        this.returnPolicyDocument = returnPolicyDocument;
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse(
                "2024-11-05", "acme-retail-mcp-server", "1.0.0",
                List.of("tools", "resources", "prompts"));
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

    @Override
    public List<McpResourceDescriptor> listResources() {
        return List.of(new McpResourceDescriptor(
                ReturnPolicyDocument.URI,
                "Acme Return Policy",
                "Acme Retail's current customer-facing return policy.",
                "text/plain"));
    }

    @Override
    public McpResource readResource(String uri) {
        if (!ReturnPolicyDocument.URI.equals(uri)) {
            throw new IllegalArgumentException("Unknown resource URI: " + uri);
        }
        return new McpResource(uri, "text/plain", returnPolicyDocument.text());
    }

    @Override
    public List<McpPromptDescriptor> listPrompts() {
        return List.of(new McpPromptDescriptor(
                SUMMARIZE_ORDER_PROMPT.name(),
                "Renders a one-sentence order-status summary prompt.",
                List.of("orderId")));
    }

    @Override
    public String getPrompt(String promptName, Map<String, String> arguments) {
        if (!SUMMARIZE_ORDER_PROMPT.name().equals(promptName)) {
            throw new IllegalArgumentException("Unknown prompt: " + promptName);
        }
        return SUMMARIZE_ORDER_PROMPT.render(arguments);
    }
}
