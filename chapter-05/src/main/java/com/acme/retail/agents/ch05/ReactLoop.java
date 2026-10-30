package com.acme.retail.agents.ch05;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Carried over from Chapter 4 -- reason, act, observe, repeat, against real tools. */
public class ReactLoop {

    private static final int MAX_STEPS = 5;
    private static final ObjectMapper JSON = new ObjectMapper();

    private final Map<String, ToolCallback> toolsByName;

    public ReactLoop(List<ToolCallback> tools) {
        this.toolsByName = tools.stream()
                .collect(Collectors.toMap(tool -> tool.getToolDefinition().name(), tool -> tool));
    }

    public ReactResult run(String question, NextStepPlanner planner) {
        List<ReactStep> history = new ArrayList<>();

        for (int i = 0; i < MAX_STEPS; i++) {
            PlannedAction action = planner.decideNext(question, history);

            if (action.finished()) {
                return new ReactResult(action.finalAnswer(), List.copyOf(history));
            }

            ToolCallback tool = toolsByName.get(action.toolName());
            if (tool == null) {
                throw new IllegalStateException("No tool registered with name: " + action.toolName());
            }

            String observation = tool.call(toJson(action.arguments()));
            history.add(new ReactStep(action.thought(), action.toolName(), observation));
        }

        throw new IllegalStateException("ReAct loop did not finish within " + MAX_STEPS + " steps");
    }

    private String toJson(Map<String, Object> arguments) {
        try {
            return JSON.writeValueAsString(arguments);
        } catch (Exception e) {
            throw new IllegalStateException("Could not encode tool arguments as JSON: " + arguments, e);
        }
    }
}
