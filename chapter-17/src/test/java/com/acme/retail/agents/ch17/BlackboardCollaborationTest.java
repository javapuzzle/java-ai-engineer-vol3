package com.acme.retail.agents.ch17;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the blackboard pattern's key structural difference from
 * delegation: neither {@link OrderAgent} nor {@link WarehouseInventoryAgent}
 * is constructed with a reference to the other anywhere in this test.
 */
class BlackboardCollaborationTest {

    private final OrderAgent orderAgent = new OrderAgent(new OrderDirectory(), null);
    private final WarehouseInventoryAgent warehouseInventoryAgent = new WarehouseInventoryAgent();

    @Test
    void twoAgentsWithNoReferenceToEachOtherCollaborateThroughTheSharedBlackboard() {
        Blackboard blackboard = new Blackboard();

        orderAgent.postOrderContext(blackboard, "ORD-9042");
        warehouseInventoryAgent.postStockFinding(blackboard, "SKU-B");

        WarehouseStockResult result = blackboard.get("stockResult", WarehouseStockResult.class).orElseThrow();
        assertThat(result.warehouse()).isEqualTo("Warehouse-East-1");
        assertThat(result.quantity()).isEqualTo(8);
    }

    @Test
    void readingAFactBeforeItsBeenPostedFailsClearlyInsteadOfGuessing() {
        Blackboard emptyBlackboard = new Blackboard();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> warehouseInventoryAgent.postStockFinding(emptyBlackboard, "SKU-A"));
    }
}
