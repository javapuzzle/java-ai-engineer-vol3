package com.acme.retail.agents.ch09.parallelization;

import com.acme.retail.agents.ch09.OrderDirectory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Fans a request for several orders out into independent, concurrently-
 * running lookups, then fans the results back in as one combined answer.
 * Each lookup has no idea any other lookup is happening -- exactly like
 * Chapter 4's tools not knowing about each other, but run at the same
 * time instead of one after another, because nothing about one order's
 * status depends on another's.
 */
@Service
public class ParallelOrderLookup {

    private final OrderDirectory orderDirectory;

    public ParallelOrderLookup(OrderDirectory orderDirectory) {
        this.orderDirectory = orderDirectory;
    }

    public String lookupAll(List<String> orderIds) {
        try (ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> futures = orderIds.stream()
                    .map(orderId -> CompletableFuture.supplyAsync(
                            () -> describeOrder(orderId), virtualThreadExecutor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.joining(" "));
        }
    }

    private String describeOrder(String orderId) {
        return orderDirectory.findById(orderId)
                .map(order -> "Order " + order.orderId() + " is currently " + order.status() + ".")
                .orElse("No order found with ID " + orderId + ".");
    }
}
