package com.acme.retail.agents.ch04;

import java.util.List;

/**
 * The one seam in this chapter that a live model would actually fill.
 * Given the original question and everything observed so far, decide
 * what to do next. This chapter's only implementation,
 * {@code ScriptedReturnEligibilityPlanner}, is deliberately scripted for
 * one specific scenario -- proving the loop's mechanics, not general
 * reasoning ability, which is exactly what a real model call would need
 * to supply in production.
 */
public interface NextStepPlanner {

    PlannedAction decideNext(String question, List<ReactStep> history);
}
