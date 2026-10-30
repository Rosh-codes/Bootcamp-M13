package com.accenture.springai_bootcamp_demo.client;

import com.accenture.springai_bootcamp_demo.entity.ChatMessage;
import com.accenture.springai_bootcamp_demo.entity.Role;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Sends persisted conversation history to the locally configured Ollama model. */
@Component
public class OllamaClient {

    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);
    private final ChatClient chatClient;

    public OllamaClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String complete(List<ChatMessage> history) {
        try {
            List<Message> messages = history.stream()
                    .map(this::toAiMessage)
                    .toList();
            String response = chatClient.prompt(new Prompt(messages))
                    .call()
                    .content();

            if (!StringUtils.hasText(response)) {
                throw new OllamaException("Ollama returned an empty response");
            }
            return response.trim();
        } catch (OllamaException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Ollama request failed", ex);
            throw new OllamaException("Failed to reach the local Ollama model: " + ex.getMessage(), ex);
        }
    }

    private Message toAiMessage(ChatMessage message) {
        return switch (message.getRole()) {
            case SYSTEM -> new SystemMessage(message.getContent());
            case USER -> new UserMessage(message.getContent());
            case ASSISTANT -> new AssistantMessage(message.getContent());
        };
    }
}
