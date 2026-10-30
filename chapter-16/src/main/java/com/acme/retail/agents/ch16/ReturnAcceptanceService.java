package com.acme.retail.agents.ch16;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accepts a return, and can undo that acceptance -- the compensating
 * action a saga needs when a later step fails after this one already
 * succeeded. Tracking acceptance as real, checkable state (not just a
 * side effect that happened once) is what makes the undo verifiable.
 */
@Service
public class ReturnAcceptanceService {

    private final Set<String> acceptedOrderIds = ConcurrentHashMap.newKeySet();
    private int acceptCallCount = 0;

    public boolean acceptReturn(String orderId) {
        acceptCallCount++;
        acceptedOrderIds.add(orderId);
        return true;
    }

    public void undoReturn(String orderId) {
        acceptedOrderIds.remove(orderId);
    }

    public boolean isReturnAccepted(String orderId) {
        return acceptedOrderIds.contains(orderId);
    }

    public int acceptCallCount() {
        return acceptCallCount;
    }
}
