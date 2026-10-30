package com.acme.retail.agents.ch08.langchain4j;

/**
 * The developer-facing interface a LangChain4j "AI Service" is built
 * from -- the whole point of that framework's model is that this
 * interface has no implementation anywhere in application code. A
 * dynamic proxy provides one at runtime.
 */
public interface OrderAssistant {

    String askAboutOrder(String question);
}
