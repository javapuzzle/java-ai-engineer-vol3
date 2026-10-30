package com.acme.retail.agents.ch18;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A hard limit enforced outside any agent's own reasoning -- never a
 * prompt instruction an agent could talk itself out of. This is the
 * exact mechanism that catches the incident this chapter opens with:
 * two refund calls, each individually beneath the human-approval
 * threshold, that together would refund more than the order's original
 * amount. Neither call is wrong on its own; only their combination is,
 * and only a guardrail that tracks cumulative state across calls can
 * see that.
 */
@Service
public class RefundGuardrail {

    private static final BigDecimal HUMAN_APPROVAL_THRESHOLD = new BigDecimal("200.00");

    private final OrderDirectory orderDirectory;
    private final Map<String, BigDecimal> refundedSoFarByOrder = new ConcurrentHashMap<>();

    public RefundGuardrail(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    public RefundDecision evaluate(RefundRequest request) {
        OrderRecord order = orderDirectory.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("No order found with ID " + request.orderId()));

        BigDecimal alreadyRefunded = refundedSoFarByOrder.getOrDefault(request.orderId(), BigDecimal.ZERO);
        BigDecimal totalAfterThisRefund = alreadyRefunded.add(request.amount());

        if (totalAfterThisRefund.compareTo(order.originalAmount()) > 0) {
            return RefundDecision.blocked("Refund would exceed the order's original amount: already refunded "
                    + alreadyRefunded + ", requested " + request.amount() + ", order total " + order.originalAmount() + ".");
        }

        if (request.amount().compareTo(HUMAN_APPROVAL_THRESHOLD) > 0 && !request.humanApproved()) {
            return RefundDecision.blocked("Refunds above " + HUMAN_APPROVAL_THRESHOLD + " require human approval.");
        }

        refundedSoFarByOrder.merge(request.orderId(), request.amount(), BigDecimal::add);
        return RefundDecision.permit();
    }
}
