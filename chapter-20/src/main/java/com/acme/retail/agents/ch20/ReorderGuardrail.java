package com.acme.retail.agents.ch20;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The same "hard limit enforced outside any agent's own reasoning"
 * discipline from Chapter 18's RefundGuardrail, applied to autonomous
 * reordering instead of refunds. This is exactly what catches this
 * capstone's incident: two monitor specialists independently flagging
 * the same SKU leads the orchestrator to attempt two reorders for it,
 * neither of which the orchestrator itself recognizes as a duplicate --
 * only a guardrail tracking cumulative state across calls can.
 */
@Service
public class ReorderGuardrail {

    private static final int MAX_DAILY_REORDER_QUANTITY = 10;

    private final Map<String, Integer> reorderedTodayBySku = new ConcurrentHashMap<>();

    public ReorderDecision evaluate(ReorderRequest request) {
        int alreadyReordered = reorderedTodayBySku.getOrDefault(request.sku(), 0);
        int totalAfterThisReorder = alreadyReordered + request.quantity();

        if (totalAfterThisReorder > MAX_DAILY_REORDER_QUANTITY) {
            return ReorderDecision.blocked("Reordering " + request.quantity() + " units of " + request.sku()
                    + " would exceed the daily reorder limit of " + MAX_DAILY_REORDER_QUANTITY
                    + " (already reordered " + alreadyReordered + " today).");
        }

        reorderedTodayBySku.merge(request.sku(), request.quantity(), Integer::sum);
        return ReorderDecision.permit();
    }
}
