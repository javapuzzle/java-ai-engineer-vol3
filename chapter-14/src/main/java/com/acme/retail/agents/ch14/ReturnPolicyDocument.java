package com.acme.retail.agents.ch14;

import org.springframework.stereotype.Service;

/**
 * Acme's current return-policy document -- read-only, addressable data
 * that a tool call was never the right shape for. Nothing here executes
 * an action; it's a fact for a client to read.
 */
@Service
public class ReturnPolicyDocument {

    public static final String URI = "acme://policies/returns";

    public String text() {
        return "Acme Retail accepts returns within 30 days of delivery for items in original "
                + "condition. Refunds are issued to the original payment method within 5-7 "
                + "business days of the returned item being received.";
    }
}
