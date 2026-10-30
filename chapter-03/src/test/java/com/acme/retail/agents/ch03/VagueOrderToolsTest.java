package com.acme.retail.agents.ch03;

import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reproduces the exact mistake Maya caught live: a vague contract that
 * silently treats a customer's name as if it were an order ID, producing
 * a confidently wrong-shaped answer -- never an exception, never an
 * obvious failure.
 */
class VagueOrderToolsTest {

    private final VagueOrderTools vagueOrderTools = new VagueOrderTools(new OrderDirectory());
    private final ToolCallback toolCallback = ToolCallbacks.from(vagueOrderTools)[0];

    @Test
    void theGeneratedSchemaSaysNothingAboutWhatTheArgumentShouldLookLike() {
        String inputSchema = toolCallback.getToolDefinition().inputSchema();

        assertThat(inputSchema)
                .as("no @ToolParam means no description at all -- a model has only the parameter name \"x\" to go on")
                .doesNotContain("order ID")
                .doesNotContain("ORD-");
    }

    @Test
    void aCustomerNamePassedInsteadOfAnOrderIdProducesAConfidentlyWrongAnswer() {
        String result = toolCallback.call("{\"x\":\"Jane Smith\"}");

        assertThat(result)
                .as("this never throws and never looks obviously wrong -- it's indistinguishable "
                        + "from a genuinely unknown order, which is exactly the danger")
                .isEqualTo("\"No order found with ID Jane Smith.\"");
    }
}
