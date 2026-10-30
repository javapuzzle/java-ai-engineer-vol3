package com.acme.retail.agents.ch16;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reserves a replacement SKU for a customer, and can release that
 * reservation. Reservation fails outright (returns false) when the SKU
 * has no stock -- exactly the failure this chapter's saga needs to
 * trigger compensation of the already-accepted return.
 */
@Service
public class ReplacementReservationService {

    private final Map<String, Integer> stockBySku = Map.of(
            "SKU-A", 5,
            "SKU-B", 0
    );

    private final Map<String, String> reservedForOrder = new ConcurrentHashMap<>();

    public boolean reserveReplacement(String orderId, String sku) {
        Integer stock = stockBySku.get(sku);
        if (stock == null || stock <= 0) {
            return false;
        }
        reservedForOrder.put(orderId, sku);
        return true;
    }

    public void releaseReservation(String orderId) {
        reservedForOrder.remove(orderId);
    }

    public boolean isReserved(String orderId) {
        return reservedForOrder.containsKey(orderId);
    }
}
