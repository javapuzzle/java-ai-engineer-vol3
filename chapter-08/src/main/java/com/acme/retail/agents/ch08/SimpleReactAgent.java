package com.acme.retail.agents.ch08;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The first real, assembled agent: a fixed set of tools, a planner that
 * decides what to do with them, and a loop that carries the conversation
 * across as many steps as a given question actually needs. Nothing about
 * this class is exotic -- it's a composition of Chapters 2-4's pieces,
 * with one new job: answering more than one shape of question through a
 * single, uniform {@link #ask(String)} entry point.
 */
@Service
public class SimpleReactAgent {

    private final ReactLoop reactLoop;
    private final NextStepPlanner planner;

    public SimpleReactAgent(OrderAgentTools orderAgentTools) {
        this.reactLoop = new ReactLoop(List.of(ToolCallbacks.from(orderAgentTools)));
        this.planner = new OrderAgentPlanner();
    }

    public ReactResult ask(String question) {
        return reactLoop.run(question, planner);
    }
}
