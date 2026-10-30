package com.acme.retail.agents.ch05;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * One agent, three questions of two genuinely different shapes -- proving
 * {@link SimpleReactAgent} decides for itself, per question, how many
 * steps are actually needed, through a single {@link SimpleReactAgent#ask}
 * entry point.
 */
class SimpleReactAgentTest {

    private final SimpleReactAgent agent = new SimpleReactAgent(new OrderAgentTools(new OrderDirectory()));

    @Test
    void aPlainStatusQuestionStopsAfterExactlyOneStep() {
        ReactResult result = agent.ask("What's the status of order ORD-7731?");

        assertThat(result.steps()).hasSize(1);
        assertThat(result.steps().get(0).toolName()).isEqualTo("getOrderStatus");
        assertThat(result.finalAnswer()).contains("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void anEligibleReturnQuestionTakesTwoStepsAndCombinesBothObservations() {
        ReactResult result = agent.ask("Is order ORD-7731 eligible for expedited return, and what's the window?");

        assertThat(result.steps()).hasSize(2);
        assertThat(result.steps().get(0).toolName()).isEqualTo("checkReturnEligibility");
        assertThat(result.steps().get(1).toolName()).isEqualTo("getReturnWindowDays");
        assertThat(result.finalAnswer()).contains("eligible for expedited return").contains("15 days");
    }

    @Test
    void anIneligibleReturnQuestionStopsAfterOneStepEvenThoughItsAReturnQuestion() {
        ReactResult result = agent.ask("Is order ORD-1200 eligible for expedited return, and what's the window?");

        assertThat(result.steps())
                .as("the same agent, asked a return-shaped question, still recognizes when a second step is pointless")
                .hasSize(1);
        assertThat(result.finalAnswer()).contains("not eligible");
    }
}
