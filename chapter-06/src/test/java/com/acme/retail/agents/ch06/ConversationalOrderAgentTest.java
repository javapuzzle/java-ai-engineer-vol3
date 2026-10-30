package com.acme.retail.agents.ch06;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConversationalOrderAgentTest {

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
    private final ConversationalOrderAgent agent =
            new ConversationalOrderAgent(new OrderAgentTools(new OrderDirectory()), chatMemory);

    @Test
    void resolvesAPronounReferenceUsingTheOrderIdRememberedFromAnEarlierTurn() {
        agent.ask("conv-1", "What's the status of order ORD-7731?");

        String secondAnswer = agent.ask("conv-1", "Is it eligible for expedited return?");

        assertThat(secondAnswer)
                .as("the second question never mentions an order ID at all -- \"it\" is resolved "
                        + "from what was remembered about this specific conversation")
                .contains("Order ORD-7731 is eligible for expedited return");
    }

    @Test
    void aDifferentConversationHasNoAccessToAnotherConversationsRememberedOrder() {
        agent.ask("conv-1", "What's the status of order ORD-7731?");

        assertThatThrownBy(() -> agent.ask("conv-2", "Is it eligible for expedited return?"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("none remembered from earlier in the conversation");
    }

    @Test
    void chatMemoryActuallyStoresBothRealTurnsOfTheConversation() {
        agent.ask("conv-1", "What's the status of order ORD-7731?");

        List<Message> transcript = chatMemory.get("conv-1");

        assertThat(transcript).hasSize(2);
        assertThat(transcript.get(0)).isInstanceOf(UserMessage.class);
        assertThat(transcript.get(0).getText()).isEqualTo("What's the status of order ORD-7731?");
        assertThat(transcript.get(1)).isInstanceOf(AssistantMessage.class);
        assertThat(transcript.get(1).getText()).contains("Order ORD-7731 is currently SHIPPED.");
    }

    @Test
    void mentioningANewOrderIdMidConversationUpdatesWhatIsRememberedForNextTime() {
        agent.ask("conv-1", "What's the status of order ORD-7731?");
        agent.ask("conv-1", "Now check order ORD-9042 instead.");

        String thirdAnswer = agent.ask("conv-1", "Is it eligible for expedited return?");

        assertThat(thirdAnswer)
                .as("the most recently mentioned order ID overrides the one remembered from the first turn")
                .contains("Order ORD-9042 is eligible for expedited return");
    }
}
