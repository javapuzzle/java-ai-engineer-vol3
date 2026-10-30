package com.acme.retail.agents.ch07;

import java.util.Map;

/** One step of a plan, decided before any execution begins. */
public record PlannedTask(String description, String toolName, Map<String, Object> arguments) {
}
