package com.acme.retail.agents.ch17;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DelegationTest {

    private final OrderAgent orderAgent = new OrderAgent(new OrderDirectory(), new WarehouseInventoryAgent());

    @Test
    void orderAgentDelegatesDirectlyToTheWarehouseSpecialistMidTask() {
        String result = orderAgent.handleNearestWarehouseStockRequest("ORD-7731", "SKU-A");

        assertThat(result).isEqualTo("12 units of SKU-A available at Warehouse-West-1.");
    }

    @Test
    void orderAgentReportsUnavailabilityWhenTheWarehouseSpecialistFindsNoStock() {
        String result = orderAgent.handleNearestWarehouseStockRequest("ORD-7731", "SKU-B");

        assertThat(result).isEqualTo("SKU-B is not available at Warehouse-West-1.");
    }
}
