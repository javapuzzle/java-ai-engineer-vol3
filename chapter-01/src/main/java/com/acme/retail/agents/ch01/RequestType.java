package com.acme.retail.agents.ch01;

/**
 * Every customer request falls into one of these two shapes. This
 * chapter's whole point is that Acme Retail's codebase, as of the end of
 * Volume 2, can only ever resolve one of them.
 */
public enum RequestType {

    /** Answerable by describing something -- a database row (Volume 1) or a retrieved document (Volume 2). */
    DESCRIBABLE,

    /** Requires taking a live action nothing in this codebase can perform yet. */
    ACTIONABLE
}
