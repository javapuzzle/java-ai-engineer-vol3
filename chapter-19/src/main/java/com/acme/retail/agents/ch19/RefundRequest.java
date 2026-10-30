package com.acme.retail.agents.ch19;

import java.math.BigDecimal;

public record RefundRequest(String orderId, BigDecimal amount, boolean humanApproved) {
}
