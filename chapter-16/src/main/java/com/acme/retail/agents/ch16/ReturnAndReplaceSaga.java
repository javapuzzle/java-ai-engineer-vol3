package com.acme.retail.agents.ch16;

import org.springframework.stereotype.Service;

/**
 * Runs the return-and-replace workflow as an explicit state graph,
 * checkpointing after every real transition and compensating the
 * already-completed return if reserving a replacement later fails.
 * {@link #resume(String)} proves the checkpoint is actually useful: it
 * continues a workflow from wherever it last recorded progress, instead
 * of re-running steps that already happened.
 */
@Service
public class ReturnAndReplaceSaga {

    private final ReturnAcceptanceService returnAcceptanceService;
    private final ReplacementReservationService replacementReservationService;
    private final CheckpointStore checkpointStore;

    public ReturnAndReplaceSaga(ReturnAcceptanceService returnAcceptanceService,
                                 ReplacementReservationService replacementReservationService,
                                 CheckpointStore checkpointStore) {
        this.returnAcceptanceService = returnAcceptanceService;
        this.replacementReservationService = replacementReservationService;
        this.checkpointStore = checkpointStore;
    }

    public SagaResult run(String workflowId, String orderId, String replacementSku) {
        checkpointStore.save(new WorkflowCheckpoint(workflowId, WorkflowState.STARTED, orderId, replacementSku));
        return advanceFromReturnAcceptance(workflowId, orderId, replacementSku);
    }

    public SagaResult resume(String workflowId) {
        WorkflowCheckpoint checkpoint = checkpointStore.find(workflowId)
                .orElseThrow(() -> new IllegalStateException("No checkpoint found for workflow: " + workflowId));

        return switch (checkpoint.state()) {
            case STARTED -> advanceFromReturnAcceptance(workflowId, checkpoint.orderId(), checkpoint.replacementSku());
            case RETURN_ACCEPTED -> advanceFromReplacementReservation(workflowId, checkpoint.orderId(), checkpoint.replacementSku());
            case REPLACEMENT_RESERVED, COMPLETED -> new SagaResult(WorkflowState.COMPLETED, "Workflow " + workflowId + " already completed.");
            case FAILED_AND_COMPENSATED -> new SagaResult(WorkflowState.FAILED_AND_COMPENSATED, "Workflow " + workflowId + " already failed and was compensated.");
        };
    }

    private SagaResult advanceFromReturnAcceptance(String workflowId, String orderId, String replacementSku) {
        returnAcceptanceService.acceptReturn(orderId);
        checkpointStore.save(new WorkflowCheckpoint(workflowId, WorkflowState.RETURN_ACCEPTED, orderId, replacementSku));
        return advanceFromReplacementReservation(workflowId, orderId, replacementSku);
    }

    private SagaResult advanceFromReplacementReservation(String workflowId, String orderId, String replacementSku) {
        boolean reserved = replacementReservationService.reserveReplacement(orderId, replacementSku);
        if (!reserved) {
            returnAcceptanceService.undoReturn(orderId);
            checkpointStore.save(new WorkflowCheckpoint(workflowId, WorkflowState.FAILED_AND_COMPENSATED, orderId, replacementSku));
            return new SagaResult(WorkflowState.FAILED_AND_COMPENSATED,
                    "Replacement " + replacementSku + " could not be reserved; return for " + orderId + " was undone.");
        }
        checkpointStore.save(new WorkflowCheckpoint(workflowId, WorkflowState.COMPLETED, orderId, replacementSku));
        return new SagaResult(WorkflowState.COMPLETED,
                "Return accepted and replacement " + replacementSku + " reserved for order " + orderId + ".");
    }
}
