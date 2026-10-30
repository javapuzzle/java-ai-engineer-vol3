package com.acme.retail.agents.ch09.routing;

/** The three outcomes a single classify-then-dispatch decision can produce. */
public enum RequestCategory {
    ORDER_STATUS,
    RETURN_ELIGIBILITY,
    UNKNOWN
}
