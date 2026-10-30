package com.acme.retail.agents.ch07;

/** The one seam a live model's planning would fill in production. */
public interface TaskPlanner {

    TaskPlan plan(String request);
}
