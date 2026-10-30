package com.acme.retail.agents.ch20;

public record OrderRecord(String orderId, String status, int minutesInProcessing, String requiredSku) {
}
