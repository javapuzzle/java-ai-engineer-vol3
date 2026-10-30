package com.acme.retail.agents.ch18;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RefundGuardrailTest {

    private final RefundGuardrail guardrail = new RefundGuardrail(new OrderDirectory());

    @Test
    void aSingleRefundWellWithinTheOrdersAmountAndBelowTheApprovalThresholdIsAllowed() {
        RefundDecision decision = guardrail.evaluate(new RefundRequest("ORD-7731", new BigDecimal("50.00"), false));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void twoIndividuallyLegitimateRefundsThatTogetherExceedTheOrdersAmountAreCaughtOnTheSecondCall() {
        RefundDecision firstDecision = guardrail.evaluate(new RefundRequest("ORD-7731", new BigDecimal("100.00"), false));
        RefundDecision secondDecision = guardrail.evaluate(new RefundRequest("ORD-7731", new BigDecimal("100.00"), false));

        assertThat(firstDecision.allowed()).isTrue();
        assertThat(secondDecision.allowed()).isFalse();
        assertThat(secondDecision.reason()).contains("exceed the order's original amount");
    }

    @Test
    void aSingleRefundAboveTheHumanApprovalThresholdIsBlockedWithoutApproval() {
        RefundDecision decision = guardrail.evaluate(new RefundRequest("ORD-9042", new BigDecimal("300.00"), false));

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("require human approval");
    }

    @Test
    void aSingleRefundAboveTheHumanApprovalThresholdIsAllowedOnceApproved() {
        RefundDecision decision = guardrail.evaluate(new RefundRequest("ORD-9042", new BigDecimal("300.00"), true));

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void anUnknownOrderIdFailsClearlyInsteadOfSilentlyAllowingOrBlocking() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> guardrail.evaluate(new RefundRequest("ORD-0000", new BigDecimal("10.00"), false)));
    }
}
