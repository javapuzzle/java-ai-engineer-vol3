package com.acme.retail.agents.ch16;

/**
 * The explicit nodes of the return-and-replace workflow's state graph.
 * Naming every state means "where are we" is always answerable by
 * reading a value, never by re-deriving it from scattered flags.
 */
public enum WorkflowState {
    STARTED,
    RETURN_ACCEPTED,
    REPLACEMENT_RESERVED,
    COMPLETED,
    FAILED_AND_COMPENSATED
}
