package com.acme.retail.agents.ch01;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantResponseServiceTest {

    private final AssistantResponseService responseService = new AssistantResponseService(
            new OrderDirectory(), new CustomerRequestClassifier());

    @Test
    void answersAStatusQuestionForAKnownOrder() {
        assertThat(responseService.respond("ORD-7731", "What's the status of order ORD-7731?"))
                .contains("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void cannotAnswerAStatusQuestionForAnUnknownOrder() {
        assertThat(responseService.respond("ORD-0000", "What's the status of order ORD-0000?")).isEmpty();
    }

    @Test
    void cannotTrackAKnownOrderLiveEvenThoughItExists() {
        assertThat(responseService.respond("ORD-7731", "Where is my order ORD-7731?"))
                .as("the order genuinely exists, and its status field is genuinely known -- "
                        + "the response is still empty, because no code path here can ever check "
                        + "a live carrier location, only describe what's already stored")
                .isEmpty();
    }

    @Test
    void cannotCancelAKnownOrder() {
        assertThat(responseService.respond("ORD-9042", "Please cancel my order ORD-9042")).isEmpty();
    }

    @Test
    void cannotCheckLiveInventoryAtAStore() {
        assertThat(responseService.respond("ORD-7731", "Can you check inventory at the downtown store?"))
                .isEmpty();
    }
}
