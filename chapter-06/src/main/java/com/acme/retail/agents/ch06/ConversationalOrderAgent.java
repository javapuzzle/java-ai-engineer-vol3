package com.acme.retail.agents.ch06;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Two genuinely different kinds of memory, used for two genuinely
 * different purposes. {@code chatMemory} is Spring AI's real {@code
 * ChatMemory} -- it stores the conversation transcript, the same thing
 * Volume 1 Chapter 13 used it for. {@code lastOrderIdByConversation} is
 * this agent's own scratchpad: a fact remembered specifically so a tool
 * call can be parameterized correctly, which {@code ChatMemory} has no
 * concept of at all -- it only ever stores {@code Message} objects, never
 * arbitrary task state.
 */
@Service
public class ConversationalOrderAgent {

    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("ORD-\\d{4}");

    private final OrderAgentTools tools;
    private final ChatMemory chatMemory;
    private final Map<String, String> lastOrderIdByConversation = new ConcurrentHashMap<>();

    public ConversationalOrderAgent(OrderAgentTools tools, ChatMemory chatMemory) {
        this.tools = tools;
        this.chatMemory = chatMemory;
    }

    public String ask(String conversationId, String question) {
        chatMemory.add(conversationId, new UserMessage(question));

        String orderId = extractOrderId(question)
                .orElseGet(() -> lastOrderIdByConversation.get(conversationId));

        if (orderId == null) {
            throw new IllegalStateException(
                    "No order ID mentioned in this question, and none remembered from earlier in the conversation.");
        }
        lastOrderIdByConversation.put(conversationId, orderId);

        String answer = question.toLowerCase(Locale.ROOT).contains("eligible")
                ? tools.checkReturnEligibility(orderId)
                : tools.getOrderStatus(orderId);

        chatMemory.add(conversationId, new AssistantMessage(answer));
        return answer;
    }

    private java.util.Optional<String> extractOrderId(String question) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(question);
        return matcher.find() ? java.util.Optional.of(matcher.group()) : java.util.Optional.empty();
    }
}
