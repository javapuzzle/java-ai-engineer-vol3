package com.acme.retail.agents.ch18;

import java.math.BigDecimal;

public record OrderRecord(String orderId, BigDecimal originalAmount) {
}
