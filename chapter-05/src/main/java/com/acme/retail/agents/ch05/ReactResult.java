package com.acme.retail.agents.ch05;

import java.util.List;

public record ReactResult(String finalAnswer, List<ReactStep> steps) {
}
