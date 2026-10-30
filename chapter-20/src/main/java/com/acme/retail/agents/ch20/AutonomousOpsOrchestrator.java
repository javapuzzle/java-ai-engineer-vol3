package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates every monitor specialist, decides what to do with each
 * finding, and enforces every remediation through {@link ReorderGuardrail}
 * -- never trusting itself to know when a reorder is safe. Deliberately
 * naive in one respect: it reorders once per finding that names a SKU,
 * without first checking whether another finding already triggered a
 * reorder for that same SKU. That's the live gap this capstone's
 * incident exercises -- composing correct specialists and a correct
 * guardrail does not, by itself, guarantee the orchestrator's own logic
 * has no blind spots; only the guardrail catches what the orchestrator
 * itself never thought to check.
 */
@Service
public class AutonomousOpsOrchestrator {

    private static final int DEFAULT_REORDER_QUANTITY = 8;

    private final OrderBacklogMonitorAgent orderBacklogMonitorAgent;
    private final InventoryAnomalyMonitorAgent inventoryAnomalyMonitorAgent;
    private final RetrievalHealthMonitorAgent retrievalHealthMonitorAgent;
    private final ReorderGuardrail reorderGuardrail;

    public AutonomousOpsOrchestrator(OrderBacklogMonitorAgent orderBacklogMonitorAgent,
                                      InventoryAnomalyMonitorAgent inventoryAnomalyMonitorAgent,
                                      RetrievalHealthMonitorAgent retrievalHealthMonitorAgent,
                                      ReorderGuardrail reorderGuardrail) {
        this.orderBacklogMonitorAgent = orderBacklogMonitorAgent;
        this.inventoryAnomalyMonitorAgent = inventoryAnomalyMonitorAgent;
        this.retrievalHealthMonitorAgent = retrievalHealthMonitorAgent;
        this.reorderGuardrail = reorderGuardrail;
    }

    public IncidentReport runIncidentScan(AgentTrace trace) {
        trace.record("OpsOrchestrator", "reasoning", "Starting ops scan");

        List<OpsFinding> backlogFindings = orderBacklogMonitorAgent.scan();
        List<OpsFinding> inventoryFindings = inventoryAnomalyMonitorAgent.scan();
        List<OpsFinding> retrievalFindings = retrievalHealthMonitorAgent.scan();

        List<OpsFinding> allFindings = new ArrayList<>();
        allFindings.addAll(backlogFindings);
        allFindings.addAll(inventoryFindings);
        allFindings.addAll(retrievalFindings);
        for (OpsFinding finding : allFindings) {
            trace.record(finding.source(), "finding", finding.description());
        }

        int reordersAllowed = 0;
        int reordersBlocked = 0;
        List<String> escalations = new ArrayList<>();

        for (OpsFinding finding : inventoryFindings) {
            boolean allowed = attemptReorder(trace, finding, escalations);
            if (allowed) {
                reordersAllowed++;
            } else {
                reordersBlocked++;
            }
        }

        for (OpsFinding finding : backlogFindings) {
            if (finding.relatedSku() == null) {
                continue;
            }
            boolean allowed = attemptReorder(trace, finding, escalations);
            if (allowed) {
                reordersAllowed++;
            } else {
                reordersBlocked++;
            }
        }

        for (OpsFinding finding : retrievalFindings) {
            trace.record("OpsOrchestrator", "escalation", "No autonomous remediation available: " + finding.description());
            escalations.add(finding.description());
        }

        IncidentReport report = new IncidentReport(allFindings, reordersAllowed, reordersBlocked, escalations);
        trace.record("OpsOrchestrator", "reasoning", report.summary());
        return report;
    }

    private boolean attemptReorder(AgentTrace trace, OpsFinding finding, List<String> escalations) {
        trace.record("OpsOrchestrator", "tool_call", "reorder(" + finding.relatedSku() + ", " + DEFAULT_REORDER_QUANTITY + ")");
        ReorderDecision decision = reorderGuardrail.evaluate(new ReorderRequest(finding.relatedSku(), DEFAULT_REORDER_QUANTITY));
        trace.record("OpsOrchestrator", "guardrail_decision", decision.allowed() ? "allowed" : "blocked: " + decision.reason());
        if (!decision.allowed()) {
            trace.record("OpsOrchestrator", "escalation", "Reorder blocked, escalating to a human: " + decision.reason());
            escalations.add(decision.reason());
        }
        return decision.allowed();
    }
}
