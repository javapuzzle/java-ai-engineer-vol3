package com.acme.retail.agents.ch20;

import java.util.List;

/**
 * A stand-in for probing the Volume 2 Knowledge Assistant's own
 * retrieval health -- no real vector store or retrieval pipeline exists
 * in this offline build (the same limitation documented throughout
 * volume2_1), so this agent takes its probe result as a constructor
 * argument rather than performing a real health check. What matters for
 * this chapter is what the orchestrator does with an unhealthy finding,
 * not how the probe itself would be implemented. Not a Spring bean --
 * its "probe result" is per-scenario, not a fixed application-wide
 * value, so it's constructed directly wherever it's needed.
 */
public class RetrievalHealthMonitorAgent {

    private final boolean healthy;

    public RetrievalHealthMonitorAgent(boolean healthy) {
        this.healthy = healthy;
    }

    public List<OpsFinding> scan() {
        if (healthy) {
            return List.of();
        }
        return List.of(new OpsFinding(
                "retrieval-health",
                "The Knowledge Assistant's retrieval pipeline is reporting degraded health.",
                "HIGH",
                null));
    }
}
