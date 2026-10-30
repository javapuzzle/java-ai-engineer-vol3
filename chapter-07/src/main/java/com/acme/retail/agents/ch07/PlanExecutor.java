package com.acme.retail.agents.ch07;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Carries out a plan decided entirely upfront -- but still checks each
 * real result before moving to the next task, and aborts rather than
 * blindly running every remaining step once an early one makes them
 * pointless. Explicit planning decides the sequence in advance; it
 * doesn't mean executing that sequence blindly.
 */
public class PlanExecutor {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final Map<String, ToolCallback> toolsByName;

    public PlanExecutor(List<ToolCallback> tools) {
        this.toolsByName = tools.stream()
                .collect(Collectors.toMap(tool -> tool.getToolDefinition().name(), tool -> tool));
    }

    public PlanExecutionResult execute(TaskPlan plan) {
        List<TaskOutcome> outcomes = new ArrayList<>();

        for (PlannedTask task : plan.tasks()) {
            ToolCallback tool = toolsByName.get(task.toolName());
            if (tool == null) {
                throw new IllegalStateException("No tool registered with name: " + task.toolName());
            }

            String observation = tool.call(toJson(task.arguments()));
            outcomes.add(new TaskOutcome(task, observation));

            if (observation.contains("not eligible")) {
                return new PlanExecutionResult(false, List.copyOf(outcomes),
                        "Plan aborted after step " + outcomes.size() + " of " + plan.tasks().size()
                                + ": order is not eligible for return.");
            }
        }

        return new PlanExecutionResult(true, List.copyOf(outcomes),
                "All " + outcomes.size() + " planned steps completed successfully.");
    }

    private String toJson(Map<String, Object> arguments) {
        try {
            return JSON.writeValueAsString(arguments);
        } catch (Exception e) {
            throw new IllegalStateException("Could not encode task arguments as JSON: " + arguments, e);
        }
    }
}
