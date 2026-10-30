package com.acme.retail.agents.ch03;

import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import static org.assertj.core.api.Assertions.assertThat;

class PreciseOrderToolsTest {

    private final PreciseOrderTools preciseOrderTools = new PreciseOrderTools(new OrderDirectory());
    private final ToolCallback toolCallback = ToolCallbacks.from(preciseOrderTools)[0];

    @Test
    void theGeneratedSchemaStatesTheExpectedFormatExplicitly() {
        String inputSchema = toolCallback.getToolDefinition().inputSchema();

        assertThat(inputSchema)
                .contains("formatted exactly like ORD-1234")
                .contains("Never a customer name or email address");
    }

    @Test
    void aCustomerNamePassedInsteadOfAnOrderIdIsRejectedCleanlyBeforeAnyLookup() {
        String result = toolCallback.call("{\"orderId\":\"Jane Smith\"}");

        assertThat(result)
                .as("the same wrong argument that silently produced a false \"not found\" in "
                        + "VagueOrderTools is caught here, before OrderDirectory is ever queried")
                .isEqualTo("\"Jane Smith\" is not a valid order ID. Order IDs are formatted like ORD-1234.");
    }

    @Test
    void aWellFormedOrderIdStillResolvesNormally() {
        String result = toolCallback.call("{\"orderId\":\"ORD-7731\"}");

        assertThat(result).isEqualTo("\"Order ORD-7731 is currently SHIPPED.\"");
    }
}
