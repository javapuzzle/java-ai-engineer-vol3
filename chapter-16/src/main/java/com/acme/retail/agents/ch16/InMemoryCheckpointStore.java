package com.acme.retail.agents.ch16;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory stand-in for a real, durable checkpoint store (a database
 * table, in production). The contract -- save and find by workflow ID --
 * is what this chapter's tests actually exercise; durability itself is
 * explicitly out of scope here.
 */
@Service
public class InMemoryCheckpointStore implements CheckpointStore {

    private final Map<String, WorkflowCheckpoint> checkpointsByWorkflowId = new ConcurrentHashMap<>();

    @Override
    public void save(WorkflowCheckpoint checkpoint) {
        checkpointsByWorkflowId.put(checkpoint.workflowId(), checkpoint);
    }

    @Override
    public Optional<WorkflowCheckpoint> find(String workflowId) {
        return Optional.ofNullable(checkpointsByWorkflowId.get(workflowId));
    }
}
