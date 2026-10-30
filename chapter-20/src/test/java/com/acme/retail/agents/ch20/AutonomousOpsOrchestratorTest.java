package com.acme.retail.agents.ch20;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AutonomousOpsOrchestratorTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    private AutonomousOpsOrchestrator orchestrator(boolean retrievalHealthy) {
        return new AutonomousOpsOrchestrator(
                new OrderBacklogMonitorAgent(new OrderDirectory()),
                new InventoryAnomalyMonitorAgent(new InventoryDirectory()),
                new RetrievalHealthMonitorAgent(retrievalHealthy),
                new ReorderGuardrail());
    }

    @Test
    void aHealthyRetrievalSystemProducesNoRetrievalEscalationAtAll() {
        AgentTrace trace = new AgentTrace("INCIDENT-1", meterRegistry);
        IncidentReport report = orchestrator(true).runIncidentScan(trace);

        assertThat(report.escalationReasons()).noneMatch(reason -> reason.contains("retrieval"));
    }

    @Test
    void anUnhealthyRetrievalSystemAlwaysEscalatesSinceNoAutonomousRemediationExists() {
        AgentTrace trace = new AgentTrace("INCIDENT-2", meterRegistry);
        IncidentReport report = orchestrator(false).runIncidentScan(trace);

        assertThat(report.escalationReasons()).anyMatch(reason -> reason.contains("degraded health"));
    }

    @Test
    void twoIndependentFindingsAboutTheSameLowStockSkuCauseTheSecondReorderAttemptToBeBlockedAndEscalated() {
        AgentTrace trace = new AgentTrace("INCIDENT-3", meterRegistry);

        IncidentReport report = orchestrator(true).runIncidentScan(trace);

        List<OpsFinding> findingsForSkuA = report.findings().stream()
                .filter(f -> "SKU-A".equals(f.relatedSku()))
                .toList();
        assertThat(findingsForSkuA).hasSize(2)
                .extracting(OpsFinding::source)
                .containsExactlyInAnyOrder("order-backlog", "inventory-anomaly");

        assertThat(report.reordersAllowed()).isEqualTo(1);
        assertThat(report.reordersBlocked()).isEqualTo(1);
        assertThat(report.escalationReasons()).anyMatch(reason -> reason.contains("daily reorder limit"));
    }

    @Test
    void theTraceLegiblyExplainsWhyTheSecondReorderWasBlocked() {
        AgentTrace trace = new AgentTrace("INCIDENT-4", meterRegistry);

        orchestrator(true).runIncidentScan(trace);

        String rendered = trace.render();
        assertThat(rendered).contains("[OpsOrchestrator]");
        assertThat(rendered).contains("blocked: Reordering");
        assertThat(rendered).contains("escalation");
    }
}
