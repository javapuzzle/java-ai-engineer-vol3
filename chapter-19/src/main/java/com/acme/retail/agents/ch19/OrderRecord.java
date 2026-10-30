package com.acme.retail.agents.ch19;

import java.math.BigDecimal;

public record OrderRecord(String orderId, BigDecimal originalAmount) {
}
