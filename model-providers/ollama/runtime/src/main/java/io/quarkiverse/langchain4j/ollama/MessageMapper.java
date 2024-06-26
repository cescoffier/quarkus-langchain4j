package io.quarkiverse.langchain4j.ollama;

import static dev.langchain4j.data.message.ContentType.IMAGE;
import static dev.langchain4j.data.message.ContentType.TEXT;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ContentType;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;

final class MessageMapper {

    private MessageMapper() {
    }

    private final static Predicate<ChatMessage> isUserMessage = chatMessage -> chatMessage instanceof UserMessage;
    private final static Predicate<UserMessage> hasImages = userMessage -> userMessage.contents().stream()
            .anyMatch(content -> IMAGE.equals(content.type()));

    static List<Message> toOllamaMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(message -> isUserMessage.test(message) && hasImages.test((UserMessage) message)
                        ? messagesWithImageSupport((UserMessage) message)
                        : otherMessages(message))
                .collect(Collectors.toList());
    }

    private static Message messagesWithImageSupport(UserMessage userMessage) {
        Map<ContentType, List<Content>> groupedContents = userMessage.contents().stream()
                .collect(Collectors.groupingBy(Content::type));

        if (groupedContents.get(TEXT).size() != 1) {
            throw new RuntimeException("Expecting single text content, but got: " + userMessage.contents());
        }

        String text = ((TextContent) groupedContents.get(TEXT).get(0)).text();

        List<ImageContent> imageContents = groupedContents.get(IMAGE).stream()
                .map(content -> (ImageContent) content)
                .collect(Collectors.toList());

        return Message.builder()
                .role(toOllamaRole(userMessage.type()))
                .content(text)
                .images(ImageUtils.base64EncodeImageList(imageContents))
                .build();
    }

    private static Message otherMessages(ChatMessage chatMessage) {
        return Message.builder()
                .role(toOllamaRole(chatMessage.type()))
                .content(chatMessage.text())
                .build();
    }

    private static Role toOllamaRole(ChatMessageType chatMessageType) {
        return switch (chatMessageType) {
            case SYSTEM -> Role.SYSTEM;
            case USER -> Role.USER;
            case AI -> Role.ASSISTANT;
            default -> throw new IllegalArgumentException("Unknown ChatMessageType: " + chatMessageType);
        };
    }
}
