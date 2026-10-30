package com.acme.retail.agents.ch14;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SummarizeOrderPromptTest {

    private final AcmeMcpServer server = new AcmeMcpServer(
            new AcmeOrderTools(new OrderDirectory()),
            new AcmeInventoryTools(new InventoryDirectory()),
            new ReturnPolicyDocument());

    @Test
    void listPromptsDescribesTheSummarizeOrderTemplateAndItsArgument() {
        var prompts = server.listPrompts();

        assertThat(prompts).hasSize(1);
        assertThat(prompts.get(0).name()).isEqualTo("summarize_order");
        assertThat(prompts.get(0).argumentNames()).containsExactly("orderId");
    }

    @Test
    void getPromptFillsInTheOrderIdPlaceholder() {
        String rendered = server.getPrompt("summarize_order", Map.of("orderId", "ORD-7731"));

        assertThat(rendered).isEqualTo(
                "Summarize the current status of order ORD-7731 for the customer in one sentence.");
    }

    @Test
    void getPromptWithAnUnknownNameThrows() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> server.getPrompt("summarize_invoice", Map.of("orderId", "ORD-7731")));
    }
}
