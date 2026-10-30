package com.acme.retail.agents.ch20;

import java.util.List;

/**
 * The outcome of one autonomous ops incident scan -- every finding
 * surfaced, how many remediation attempts were allowed versus blocked
 * by a guardrail, and a plain-language reason for every escalation to a
 * human. This is what a human on call actually needs after the fact,
 * not a raw dump of every trace event.
 */
public record IncidentReport(
        List<OpsFinding> findings,
        int reordersAllowed,
        int reordersBlocked,
        List<String> escalationReasons) {

    public String summary() {
        return "Scan found " + findings.size() + " finding(s); "
                + reordersAllowed + " reorder(s) allowed, "
                + reordersBlocked + " reorder(s) blocked, "
                + escalationReasons.size() + " escalation(s) to a human.";
    }
}
