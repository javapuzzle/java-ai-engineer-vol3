package com.acme.retail.agents.ch09.chaining;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves chaining's core discipline: the same steps, in a different
 * order, produce a genuinely different (and wrong) result -- ordering
 * isn't a formality, it's the entire contract.
 */
class PromptChainTest {

    @Test
    void translateThenClassifyCorrectlyRecognizesTheSpanishQuestion() {
        PromptChain chain = new PromptChain(List.of(
                new SpanishToEnglishTranslationStep(),
                new ClassificationStep()
        ));

        String result = chain.run("¿Dónde está mi pedido?");

        assertThat(result).contains("[intent=order-status]");
    }

    @Test
    void classifyingBeforeTranslatingFailsToRecognizeTheSameQuestion() {
        PromptChain wrongOrderChain = new PromptChain(List.of(
                new ClassificationStep(),
                new SpanishToEnglishTranslationStep()
        ));

        String result = wrongOrderChain.run("¿Dónde está mi pedido?");

        assertThat(result)
                .as("ClassificationStep only recognizes English phrasing -- running it before "
                        + "translation means it never gets the chance to see the English version at all")
                .contains("[intent=unknown]");
    }
}
