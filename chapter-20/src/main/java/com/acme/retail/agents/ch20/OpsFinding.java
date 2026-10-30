package com.acme.retail.agents.ch20;

/**
 * One thing an ops monitor specialist noticed -- deliberately generic
 * enough to hold a backlog finding, an inventory anomaly, or a
 * retrieval-health probe result, all in the same shape. {@code
 * relatedSku} is null when a finding has no associated SKU (retrieval
 * health, for instance).
 */
public record OpsFinding(String source, String description, String severity, String relatedSku) {
}
