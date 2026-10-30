package com.acme.retail.agents.ch02;

import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves both halves of tool calling without ever calling a live model.
 * {@code ToolCallbacks.from(...)} is exactly the mechanism Spring AI uses
 * internally to turn an {@code @Tool}-annotated object into what a
 * {@code ChatClient} would register with a model -- the model's decision
 * to call the tool is the one thing left out here, because it's a live
 * reasoning step, not a mechanical one.
 */
class OrderStatusToolsTest {

    private final OrderStatusTools orderStatusTools = new OrderStatusTools(new OrderDirectory());
    private final ToolCallback toolCallback = ToolCallbacks.from(orderStatusTools)[0];

    @Test
    void discoversExactlyOneToolOnThisObject() {
        assertThat(ToolCallbacks.from(orderStatusTools)).hasSize(1);
    }

    @Test
    void theGeneratedToolDefinitionCarriesTheAnnotatedDescription() {
        assertThat(toolCallback.getToolDefinition().name()).isEqualTo("getOrderStatus");
        assertThat(toolCallback.getToolDefinition().description())
                .isEqualTo("Look up the last known status Acme Retail recorded for a customer's order, by order ID.");
    }

    @Test
    void theGeneratedJsonSchemaDescribesTheOrderIdParameter() {
        String inputSchema = toolCallback.getToolDefinition().inputSchema();

        assertThat(inputSchema)
                .as("Spring AI generates this schema from the method signature and @ToolParam -- "
                        + "this is literally what a model would be shown to decide how to call the tool")
                .contains("orderId")
                .contains("The Acme Retail order ID, formatted like ORD-1234.");
    }

    @Test
    void callingTheToolWithJsonArgumentsActuallyInvokesTheRealMethod() {
        String result = toolCallback.call("{\"orderId\":\"ORD-7731\"}");

        assertThat(result).contains("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void callingTheToolForAnUnknownOrderReturnsTheMethodsOwnFallbackText() {
        String result = toolCallback.call("{\"orderId\":\"ORD-0000\"}");

        assertThat(result).contains("No order found with ID ORD-0000.");
    }
}
