package com.acme.retail.agents.ch10;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnLabelReflectorTest {

    private final DraftLabelGenerator draftLabelGenerator = new DraftLabelGenerator();
    private final ReturnLabelReflector reflector = new ReturnLabelReflector();
    private final OrderDirectory orderDirectory = new OrderDirectory();

    @Test
    void aDescriptionThatContradictsTheNaiveDraftRevisesItToTheItemTheCustomerActuallyMeant() {
        OrderWithItems order = orderDirectory.findById("ORD-7731").orElseThrow();
        DraftReturnLabel draft = draftLabelGenerator.draft(order);
        assertThat(draft.chosenItem().description()).isEqualTo("Blue Blender");

        ReturnLabelResult result = reflector.reflect(
                "I want to return the red blender I bought last week", draft, order);

        assertThat(result.needsHumanReview()).isFalse();
        assertThat(result.item().description()).isEqualTo("Red Blender");
    }

    @Test
    void aRequestThatDoesNotDescribeAnyItemLeavesTheNaiveDraftAsTheBestAvailableGuess() {
        OrderWithItems order = orderDirectory.findById("ORD-7731").orElseThrow();
        DraftReturnLabel draft = draftLabelGenerator.draft(order);

        ReturnLabelResult result = reflector.reflect(
                "Please process a return for order ORD-7731", draft, order);

        assertThat(result.needsHumanReview()).isFalse();
        assertThat(result.item().description()).isEqualTo("Blue Blender");
    }

    @Test
    void aDescriptionThatMatchesNoItemOnTheOrderIsFlaggedForHumanReviewInsteadOfGuessed() {
        OrderWithItems order = orderDirectory.findById("ORD-7731").orElseThrow();
        DraftReturnLabel draft = draftLabelGenerator.draft(order);

        ReturnLabelResult result = reflector.reflect(
                "I'd like to return the green one", draft, order);

        assertThat(result.needsHumanReview()).isTrue();
        assertThat(result.item()).isNull();
    }

    @Test
    void aDraftThatAlreadyMatchesTheDescriptionIsConfirmedUnchanged() {
        OrderWithItems order = orderDirectory.findById("ORD-9042").orElseThrow();
        DraftReturnLabel draft = draftLabelGenerator.draft(order);
        assertThat(draft.chosenItem().description()).isEqualTo("Wireless Headphones");

        ReturnLabelResult result = reflector.reflect(
                "Please return my wireless headphones, I don't need them anymore", draft, order);

        assertThat(result.needsHumanReview()).isFalse();
        assertThat(result.item().description()).isEqualTo("Wireless Headphones");
    }
}
