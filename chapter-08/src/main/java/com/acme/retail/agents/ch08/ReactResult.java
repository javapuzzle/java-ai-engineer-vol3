package com.acme.retail.agents.ch08;

import java.util.List;

public record ReactResult(String finalAnswer, List<ReactStep> steps) {
}
