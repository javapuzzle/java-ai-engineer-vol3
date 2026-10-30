package com.acme.retail.agents.ch07;

import org.junit.jupiter.api.Test;
import org.springframework.ai.support.ToolCallbacks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanExecutorTest {

    private final ReturnProcessingTools tools = new ReturnProcessingTools(new OrderDirectory());
    private final PlanExecutor executor = new PlanExecutor(List.of(ToolCallbacks.from(tools)));
    private final ReturnProcessingPlanner planner = new ReturnProcessingPlanner();

    @Test
    void thePlanIsFullyDecidedBeforeAnyTaskRuns() {
        TaskPlan plan = planner.plan("Please process a return for order ORD-7731 end to end.");

        assertThat(plan.tasks())
                .as("all four steps exist, in order, before PlanExecutor has run any of them")
                .extracting(PlannedTask::toolName)
                .containsExactly("checkReturnEligibility", "generateReturnLabel", "notifyCustomer", "restockInventory");
    }

    @Test
    void anEligibleOrderCompletesAllFourStepsInOrder() {
        TaskPlan plan = planner.plan("Please process a return for order ORD-7731 end to end.");

        PlanExecutionResult result = executor.execute(plan);

        assertThat(result.completed()).isTrue();
        assertThat(result.outcomes()).hasSize(4);
        assertThat(result.outcomes().get(1).observation()).contains("Return label RL-ORD-7731");
        assertThat(result.outcomes().get(3).observation()).contains("category ELECTRONICS");
    }

    @Test
    void anIneligibleOrderAbortsAfterTheFirstStepAndNeverGeneratesALabel() {
        TaskPlan plan = planner.plan("Please process a return for order ORD-1200 end to end.");

        PlanExecutionResult result = executor.execute(plan);

        assertThat(result.completed()).isFalse();
        assertThat(result.outcomes())
                .as("generateReturnLabel, notifyCustomer, and restockInventory never ran -- "
                        + "the plan was fully decided upfront, but execution still stopped for a real reason")
                .hasSize(1);
        assertThat(result.summary()).contains("Plan aborted after step 1 of 4");
    }
}
