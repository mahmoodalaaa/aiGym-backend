package com.example.aiGym.chat.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatModel chatModel;

    private static final String SYSTEM_PROMPT = """
            You are GYM AI, a premium AI fitness coach. 
            Your goal is to provide expert advice on workouts, nutrition, and recovery. 
            Be encouraging, professional, and concise. 
            If you don't know something, be honest. 
            Always prioritize safety.
            """;

    public String generateResponse(String userMessage) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_PROMPT);
        Message systemMessage = systemPromptTemplate.createMessage();
        Message userMsg = new UserMessage(userMessage);

        Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}
