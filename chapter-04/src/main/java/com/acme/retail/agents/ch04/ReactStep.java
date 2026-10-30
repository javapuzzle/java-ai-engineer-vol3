package com.acme.retail.agents.ch04;

/**
 * One completed step of a ReAct loop: what the planner was thinking,
 * which tool it chose, and what actually came back when that tool was
 * really called.
 */
public record ReactStep(String thought, String toolName, String observation) {
}
