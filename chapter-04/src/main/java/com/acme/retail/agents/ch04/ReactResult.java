package com.acme.retail.agents.ch04;

import java.util.List;

/** The final answer a {@link ReactLoop} produced, plus the full trail of steps it took to get there. */
public record ReactResult(String finalAnswer, List<ReactStep> steps) {
}
