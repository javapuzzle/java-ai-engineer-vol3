package com.acme.retail.agents.ch08.langchain4j;

import com.acme.retail.agents.ch08.SimpleReactAgent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Models LangChain4j's real, well-documented core mechanism -- {@code
 * AiServices.builder(SomeInterface.class).tools(...).build()} implements
 * a plain developer-written interface with a JDK dynamic proxy at
 * runtime, so calling code never sees an agent object at all, only its
 * own interface.
 *
 * <p><b>Why this is hand-written instead of importing the real
 * library:</b> {@code dev.langchain4j:langchain4j-core} is not
 * resolvable in this offline build environment -- confirmed absent
 * everywhere in the local Maven cache, not even a stale
 * {@code .pom.lastUpdated} marker (a stronger unavailability than
 * {@code spring-ai-vector-store}'s). This class does not claim to
 * reproduce LangChain4j's exact annotation syntax or internal prompt
 * construction; it demonstrates the one architectural mechanism that is
 * both stable, well-known, and independently verifiable in any JDK: a
 * dynamic proxy standing between a caller and the real agent underneath.
 */
public final class DeclarativeAiServiceFactory {

    private DeclarativeAiServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> aiServiceInterface, SimpleReactAgent agent) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getName().equals("toString")) {
                return "DeclarativeAiService[" + aiServiceInterface.getSimpleName() + "]";
            }
            String question = (String) args[0];
            return agent.ask(question).finalAnswer();
        };

        return (T) Proxy.newProxyInstance(
                aiServiceInterface.getClassLoader(),
                new Class<?>[]{aiServiceInterface},
                handler);
    }
}
