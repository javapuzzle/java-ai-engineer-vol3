package com.acme.retail.agents.ch20;

import com.acme.retail.agents.ch20.mcp.McpInitializeRequest;
import com.acme.retail.agents.ch20.mcp.McpInitializeResponse;
import com.acme.retail.agents.ch20.mcp.McpServer;
import com.acme.retail.agents.ch20.mcp.McpToolCallRequest;
import com.acme.retail.agents.ch20.mcp.McpToolCallResult;
import com.acme.retail.agents.ch20.mcp.McpToolDescriptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Exposes the ops orchestrator's incident scan as a real MCP tool, using
 * the same hand-written protocol stand-in introduced in Chapter 11 --
 * any compliant client, not just Acme's own on-call tooling, can trigger
 * a scan and read back a summary. Composing the MCP layer into the
 * capstone is what makes this genuinely "MCP-exposed capabilities," not
 * just an internal method call dressed up in the volume's vocabulary.
 */
@Service
public class OpsMcpServer implements McpServer {

    private static final String RUN_INCIDENT_SCAN = "runIncidentScan";

    private final AutonomousOpsOrchestrator orchestrator;
    private final MeterRegistry meterRegistry;

    public OpsMcpServer(AutonomousOpsOrchestrator orchestrator, MeterRegistry meterRegistry) {
        this.orchestrator = orchestrator;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public McpInitializeResponse initialize(McpInitializeRequest request) {
        return new McpInitializeResponse("2024-11-05", "acme-autonomous-ops-mcp-server", "1.0.0", List.of("tools"));
    }

    @Override
    public List<McpToolDescriptor> listTools() {
        return List.of(new McpToolDescriptor(
                RUN_INCIDENT_SCAN,
                "Runs a full autonomous ops incident scan across order backlog, inventory, and retrieval health.",
                "{\"type\":\"object\",\"properties\":{},\"required\":[]}"));
    }

    @Override
    public McpToolCallResult callTool(McpToolCallRequest request) {
        if (!RUN_INCIDENT_SCAN.equals(request.toolName())) {
            return McpToolCallResult.error("Unknown tool: " + request.toolName());
        }
        AgentTrace trace = new AgentTrace(UUID.randomUUID().toString(), meterRegistry);
        IncidentReport report = orchestrator.runIncidentScan(trace);
        return McpToolCallResult.ok(report.summary());
    }
}
