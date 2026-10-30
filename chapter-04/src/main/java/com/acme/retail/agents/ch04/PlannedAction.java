package com.acme.retail.agents.ch04;

import java.util.Map;

/**
 * What a {@link NextStepPlanner} decides to do next: either call one more
 * tool, or stop and answer. In production this decision comes from a live
 * model's reasoning over the conversation so far; nothing about this
 * shape changes when it does.
 */
public record PlannedAction(boolean finished, String thought, String toolName,
                             Map<String, Object> arguments, String finalAnswer) {

    public static PlannedAction callTool(String thought, String toolName, Map<String, Object> arguments) {
        return new PlannedAction(false, thought, toolName, arguments, null);
    }

    public static PlannedAction finish(String finalAnswer) {
        return new PlannedAction(true, null, null, null, finalAnswer);
    }
}
