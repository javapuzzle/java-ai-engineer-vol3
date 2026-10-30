package com.acme.retail.agents.ch18;

import java.math.BigDecimal;

public record RefundRequest(String orderId, BigDecimal amount, boolean humanApproved) {
}
