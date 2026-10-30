package com.acme.retail.agents.ch16;

import java.util.Optional;

/**
 * Where a workflow's progress is persisted. A real deployment would back
 * this with a database row per workflow instance; what matters for this
 * chapter is the contract -- save a checkpoint after every real state
 * transition, and be able to find it again after a crash.
 */
public interface CheckpointStore {

    void save(WorkflowCheckpoint checkpoint);

    Optional<WorkflowCheckpoint> find(String workflowId);
}
