package com.acme.retail.agents.ch08;

import java.util.Map;

public record PlannedAction(boolean finished, String thought, String toolName,
                             Map<String, Object> arguments, String finalAnswer) {

    public static PlannedAction callTool(String thought, String toolName, Map<String, Object> arguments) {
        return new PlannedAction(false, thought, toolName, arguments, null);
    }

    public static PlannedAction finish(String finalAnswer) {
        return new PlannedAction(true, null, null, null, finalAnswer);
    }
}
