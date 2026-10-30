package com.acme.retail.agents.ch08;

import java.util.List;

public interface NextStepPlanner {

    PlannedAction decideNext(String question, List<ReactStep> history);
}
