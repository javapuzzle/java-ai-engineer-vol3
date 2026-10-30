package com.acme.retail.agents.ch16;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnAndReplaceSagaTest {

    private final ReturnAcceptanceService returnAcceptanceService = new ReturnAcceptanceService();
    private final ReplacementReservationService replacementReservationService = new ReplacementReservationService();
    private final InMemoryCheckpointStore checkpointStore = new InMemoryCheckpointStore();
    private final ReturnAndReplaceSaga saga =
            new ReturnAndReplaceSaga(returnAcceptanceService, replacementReservationService, checkpointStore);

    @Test
    void aHappyPathSagaCompletesAndLeavesTheReturnAcceptedAndTheReplacementReserved() {
        SagaResult result = saga.run("WF-1", "ORD-7731", "SKU-A");

        assertThat(result.finalState()).isEqualTo(WorkflowState.COMPLETED);
        assertThat(returnAcceptanceService.isReturnAccepted("ORD-7731")).isTrue();
        assertThat(replacementReservationService.isReserved("ORD-7731")).isTrue();
        assertThat(checkpointStore.find("WF-1")).contains(
                new WorkflowCheckpoint("WF-1", WorkflowState.COMPLETED, "ORD-7731", "SKU-A"));
    }

    @Test
    void aFailedReservationCompensatesTheAlreadyAcceptedReturn() {
        SagaResult result = saga.run("WF-2", "ORD-9042", "SKU-B");

        assertThat(result.finalState()).isEqualTo(WorkflowState.FAILED_AND_COMPENSATED);
        assertThat(result.summary()).contains("was undone");
        assertThat(returnAcceptanceService.isReturnAccepted("ORD-9042")).isFalse();
        assertThat(replacementReservationService.isReserved("ORD-9042")).isFalse();
    }

    @Test
    void resumingFromACheckpointContinuesTheWorkflowWithoutRepeatingTheAlreadyCompletedStep() {
        checkpointStore.save(new WorkflowCheckpoint("WF-3", WorkflowState.RETURN_ACCEPTED, "ORD-7731", "SKU-A"));
        returnAcceptanceService.acceptReturn("ORD-7731");
        int acceptCallCountBeforeResume = returnAcceptanceService.acceptCallCount();

        SagaResult result = saga.resume("WF-3");

        assertThat(result.finalState()).isEqualTo(WorkflowState.COMPLETED);
        assertThat(replacementReservationService.isReserved("ORD-7731")).isTrue();
        assertThat(returnAcceptanceService.acceptCallCount()).isEqualTo(acceptCallCountBeforeResume);
    }

    @Test
    void resumingAWorkflowWithNoCheckpointFailsClearlyInsteadOfGuessing() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> saga.resume("WF-DOES-NOT-EXIST"));
    }
}
