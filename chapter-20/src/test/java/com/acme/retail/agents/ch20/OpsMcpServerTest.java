package com.acme.retail.agents.ch20;

import com.acme.retail.agents.ch20.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch20.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch20.mcp.McpToolCallResult;
import com.acme.retail.agents.ch20.mcp.McpToolDescriptor;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OpsMcpServerTest {

    private final AutonomousOpsOrchestrator orchestrator = new AutonomousOpsOrchestrator(
            new OrderBacklogMonitorAgent(new OrderDirectory()),
            new InventoryAnomalyMonitorAgent(new InventoryDirectory()),
            new RetrievalHealthMonitorAgent(true),
            new ReorderGuardrail());

    private final OpsMcpServer server = new OpsMcpServer(orchestrator, new SimpleMeterRegistry());

    @Test
    void anyCompliantClientCanDiscoverAndRunTheIncidentScanTool() {
        server.initialize(new McpInitializeRequest("acme-oncall-dashboard", "1.0"));
        List<McpToolDescriptor> tools = server.listTools();

        assertThat(tools).extracting(McpToolDescriptor::name).containsExactly("runIncidentScan");

        McpToolCallResult result = server.callTool(new McpToolCallRequest("runIncidentScan", Map.of()));

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).contains("finding(s)");
    }

    @Test
    void callingAnUnknownToolReturnsAnErrorResult() {
        McpToolCallResult result = server.callTool(new McpToolCallRequest("deleteAllOrders", Map.of()));

        assertThat(result.isError()).isTrue();
    }
}
