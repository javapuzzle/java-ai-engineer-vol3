package com.acme.retail.agents.ch10;

import org.springframework.stereotype.Service;

/**
 * The naive first pass: always draft a label for the order's first item.
 * This is deliberately the exact shortcut that shipped the real bug --
 * on its own, with no reflection step checking it, this class is wrong
 * every time the customer meant a different item on a multi-item order.
 */
@Service
public class DraftLabelGenerator {

    public DraftReturnLabel draft(OrderWithItems order) {
        OrderItem firstItem = order.items().get(0);
        return new DraftReturnLabel(order.orderId(), firstItem,
                "Defaulted to the first item on the order.");
    }
}
