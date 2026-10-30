package com.acme.retail.agents.ch15;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnAndReplacementOrchestratorTest {

    private final OrderDirectory orderDirectory = new OrderDirectory();
    private final ReturnAgent returnAgent = new ReturnAgent(orderDirectory);

    @Test
    void anEligibleOrderWithReplacementInStockCombinesBothSpecialistsAnswers() {
        InventoryAgent inventoryAgent = new InventoryAgent(new InventoryDirectory());
        ReturnAndReplacementOrchestrator orchestrator =
                new ReturnAndReplacementOrchestrator(returnAgent, inventoryAgent);

        String result = orchestrator.handleReturnAndReplacement("ORD-7731", "SKU-A");

        assertThat(result).contains("is eligible for return");
        assertThat(result).contains("42 units of SKU-A in stock");
    }

    @Test
    void anEligibleOrderWithAnOutOfStockReplacementStillReportsBothAnswers() {
        InventoryAgent inventoryAgent = new InventoryAgent(new InventoryDirectory());
        ReturnAndReplacementOrchestrator orchestrator =
                new ReturnAndReplacementOrchestrator(returnAgent, inventoryAgent);

        String result = orchestrator.handleReturnAndReplacement("ORD-7731", "SKU-B");

        assertThat(result).contains("is eligible for return");
        assertThat(result).contains("currently out of stock");
    }

    @Test
    void anIneligibleOrderNeverCausesTheOrchestratorToConsultTheInventorySpecialistAtAll() {
        CallCountingInventoryAgent inventoryAgentSpy = new CallCountingInventoryAgent(new InventoryDirectory());
        ReturnAndReplacementOrchestrator orchestrator =
                new ReturnAndReplacementOrchestrator(returnAgent, inventoryAgentSpy);

        String result = orchestrator.handleReturnAndReplacement("ORD-1200", "SKU-A");

        assertThat(result).contains("is not eligible for return");
        assertThat(inventoryAgentSpy.checkStockCallCount).isZero();
    }

    /**
     * A hand-written counting subclass standing in for a mocking
     * framework -- this environment's JDK cannot self-attach Mockito's
     * inline mock maker, and a plain subclass proves the same thing a
     * mock's verify() would: the orchestrator never even attempts to
     * reach the inventory specialist for an ineligible order.
     */
    private static class CallCountingInventoryAgent extends InventoryAgent {
        int checkStockCallCount = 0;

        CallCountingInventoryAgent(InventoryDirectory inventoryDirectory) {
            super(inventoryDirectory);
        }

        @Override
        public String checkStock(String sku) {
            checkStockCallCount++;
            return super.checkStock(sku);
        }
    }
}
