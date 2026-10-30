package com.acme.retail.agents.ch16;

/**
 * A persisted snapshot of one workflow instance's progress -- exactly
 * enough to answer "where are we" and "with what data" if the process
 * running this workflow crashes and needs to resume rather than restart.
 */
public record WorkflowCheckpoint(String workflowId, WorkflowState state, String orderId, String replacementSku) {
}
