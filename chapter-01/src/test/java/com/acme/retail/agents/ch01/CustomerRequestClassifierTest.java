package com.acme.retail.agents.ch01;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRequestClassifierTest {

    private final CustomerRequestClassifier classifier = new CustomerRequestClassifier();

    @Test
    void classifiesOrderStatusQuestionAsDescribable() {
        assertThat(classifier.classify("What's the status of order ORD-7731?"))
                .isEqualTo(RequestType.DESCRIBABLE);
    }

    @Test
    void classifiesWhereIsMyOrderAsActionable() {
        assertThat(classifier.classify("Where is my order ORD-7731?"))
                .isEqualTo(RequestType.ACTIONABLE);
    }

    @Test
    void classifiesCancelRequestAsActionable() {
        assertThat(classifier.classify("Please cancel my order ORD-9042"))
                .isEqualTo(RequestType.ACTIONABLE);
    }

    @Test
    void classifiesLiveInventoryCheckAsActionable() {
        assertThat(classifier.classify("Can you check inventory at the downtown store?"))
                .isEqualTo(RequestType.ACTIONABLE);
    }
}
