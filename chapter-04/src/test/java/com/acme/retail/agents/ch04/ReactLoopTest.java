package com.acme.retail.agents.ch04;

import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the ReAct loop's mechanics using real tool executions -- neither
 * tool has any idea the other one exists; only the loop, driven by the
 * scripted planner, chains them.
 */
class ReactLoopTest {

    private final ReturnEligibilityTools tools = new ReturnEligibilityTools(new OrderDirectory());
    private final List<ToolCallback> toolCallbacks = List.of(ToolCallbacks.from(tools));
    private final ReactLoop reactLoop = new ReactLoop(toolCallbacks);

    @Test
    void anEligibleElectronicsOrderTakesExactlyTwoRealToolCallsInOrder() {
        ReactResult result = reactLoop.run(
                "Is order ORD-7731 eligible for expedited return, and if so, what's the window?",
                new ScriptedReturnEligibilityPlanner());

        assertThat(result.steps()).hasSize(2);
        assertThat(result.steps().get(0).toolName()).isEqualTo("checkReturnEligibility");
        assertThat(result.steps().get(0).observation())
                .contains("eligible for expedited return")
                .contains("Category: ELECTRONICS");
        assertThat(result.steps().get(1).toolName()).isEqualTo("getReturnWindowDays");
        assertThat(result.steps().get(1).observation()).contains("15 days");

        assertThat(result.finalAnswer())
                .as("the final answer combines both real observations -- neither tool alone had this answer")
                .contains("eligible for expedited return")
                .contains("15 days");
    }

    @Test
    void anEligibleApparelOrderUsesTheApparelWindowNotElectronics() {
        ReactResult result = reactLoop.run(
                "Is order ORD-9042 eligible for expedited return, and if so, what's the window?",
                new ScriptedReturnEligibilityPlanner());

        assertThat(result.finalAnswer()).contains("30 days");
    }

    @Test
    void aCancelledOrderStopsAfterOneStepAndNeverAsksForAReturnWindow() {
        ReactResult result = reactLoop.run(
                "Is order ORD-1200 eligible for expedited return, and if so, what's the window?",
                new ScriptedReturnEligibilityPlanner());

        assertThat(result.steps())
                .as("the loop reasons about what it just observed -- an ineligible order never triggers the second call at all")
                .hasSize(1);
        assertThat(result.finalAnswer()).contains("not eligible");
    }
}
