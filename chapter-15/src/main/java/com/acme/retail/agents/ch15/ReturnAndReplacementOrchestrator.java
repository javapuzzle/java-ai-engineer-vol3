package com.acme.retail.agents.ch15;

import org.springframework.stereotype.Service;

/**
 * Coordinates the three narrow-scope specialists to answer one compound
 * customer request -- "return this and check if a replacement is in
 * stock" -- without any specialist knowing the other two exist. The
 * orchestrator alone decides the sequence, and specifically decides
 * NOT to call the inventory specialist at all when the return specialist
 * already rules the request out.
 */
@Service
public class ReturnAndReplacementOrchestrator {

    private final ReturnAgent returnAgent;
    private final InventoryAgent inventoryAgent;

    public ReturnAndReplacementOrchestrator(ReturnAgent returnAgent, InventoryAgent inventoryAgent) {
        this.returnAgent = returnAgent;
        this.inventoryAgent = inventoryAgent;
    }

    public String handleReturnAndReplacement(String orderId, String replacementSku) {
        if (!returnAgent.isEligibleForReturn(orderId)) {
            return returnAgent.explainEligibility(orderId);
        }
        String eligibility = returnAgent.explainEligibility(orderId);
        String stock = inventoryAgent.checkStock(replacementSku);
        return eligibility + " " + stock;
    }
}
