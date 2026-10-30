package com.acme.retail.agents.ch01;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The full extent of what Acme Retail's assistant can do, at the start of
 * Volume 3: describe exactly what {@link OrderDirectory} already
 * recorded, and nothing else. This class has no way to cancel an order,
 * check a live carrier's current transit location, or check real-time
 * inventory at a specific store -- those all require performing an
 * action against a system this class has no path to at all.
 *
 * <p>Note what this method deliberately does <em>not</em> do: for an
 * {@link RequestType#ACTIONABLE} request, it never even looks the order
 * up. Recognizing that a request needs an action, and being able to
 * perform that action, are different capabilities -- this class only has
 * the first one.
 */
@Service
public class AssistantResponseService {

    private final OrderDirectory orderDirectory;
    private final CustomerRequestClassifier classifier;

    public AssistantResponseService(OrderDirectory orderDirectory, CustomerRequestClassifier classifier) {
        this.orderDirectory = orderDirectory;
        this.classifier = classifier;
    }

    public Optional<String> respond(String orderId, String request) {
        if (classifier.classify(request) != RequestType.DESCRIBABLE) {
            return Optional.empty();
        }

        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".");
    }
}
