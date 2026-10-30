package com.acme.retail.agents.ch08.langchain4j;

import com.acme.retail.agents.ch08.OrderAgentTools;
import com.acme.retail.agents.ch08.OrderDirectory;
import com.acme.retail.agents.ch08.ReactResult;
import com.acme.retail.agents.ch08.SimpleReactAgent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Same real agent, same real tools, two calling conventions. The Spring
 * AI side calls {@code agent.ask(...)} directly and gets back a {@link
 * ReactResult}. The LangChain4j-style side calls a method on the
 * developer's own interface and never sees an agent object at all --
 * {@link DeclarativeAiServiceFactory}'s dynamic proxy is what's actually
 * running underneath.
 */
class DeclarativeAiServiceFactoryTest {

    private final SimpleReactAgent agent = new SimpleReactAgent(new OrderAgentTools(new OrderDirectory()));

    @Test
    void theDeclarativeInterfaceProducesTheIdenticalAnswerToCallingTheAgentDirectly() {
        ReactResult springAiStyleResult = agent.ask("What's the status of order ORD-7731?");

        OrderAssistant assistant = DeclarativeAiServiceFactory.create(OrderAssistant.class, agent);
        String declarativeAnswer = assistant.askAboutOrder("What's the status of order ORD-7731?");

        assertThat(declarativeAnswer)
                .as("both calling conventions reach the same real agent and the same real tools -- "
                        + "only how the caller invokes it differs")
                .isEqualTo(springAiStyleResult.finalAnswer());
    }

    @Test
    void theCallingCodeNeverReferencesSimpleReactAgentAtAll() {
        OrderAssistant assistant = DeclarativeAiServiceFactory.create(OrderAssistant.class, agent);

        String answer = assistant.askAboutOrder("Is order ORD-7731 eligible for expedited return, and what's the window?");

        assertThat(answer)
                .as("the calling code above only ever mentions OrderAssistant -- "
                        + "the agent and the ReAct loop underneath are invisible to it")
                .contains("eligible for expedited return")
                .contains("15 days");
    }

    @Test
    void theProxyImplementsExactlyTheDeveloperWrittenInterface() {
        OrderAssistant assistant = DeclarativeAiServiceFactory.create(OrderAssistant.class, agent);

        assertThat(assistant).isInstanceOf(OrderAssistant.class);
        assertThat(assistant.toString()).contains("OrderAssistant");
    }
}
