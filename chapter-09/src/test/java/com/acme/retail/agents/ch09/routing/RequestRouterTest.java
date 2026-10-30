package com.acme.retail.agents.ch09.routing;

import com.acme.retail.agents.ch09.OrderDirectory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** One classification, one dispatch -- no loop, no re-evaluation. */
class RequestRouterTest {

    private final RequestRouter router = new RequestRouter(new RequestCategoryClassifier(), new OrderDirectory());

    @Test
    void routesAStatusQuestionToTheStatusHandler() {
        assertThat(router.route("What's the status of order ORD-7731?"))
                .isEqualTo("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void routesAReturnEligibilityQuestionToTheEligibilityHandler() {
        assertThat(router.route("Is order ORD-1200 eligible for return?"))
                .isEqualTo("Order ORD-1200 is not eligible for return.");
    }

    @Test
    void routesAnUnrecognizedRequestToTheFallbackHandler() {
        assertThat(router.route("Do you sell gift cards?"))
                .isEqualTo("I'm not able to help with that request yet.");
    }
}
