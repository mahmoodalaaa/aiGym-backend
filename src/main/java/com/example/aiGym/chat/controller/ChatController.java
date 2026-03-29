package com.example.aiGym.chat.controller;

import com.example.aiGym.chat.dto.ChatRequest;
import com.example.aiGym.chat.dto.ChatResponse;
import com.example.aiGym.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String responseContent = chatService.generateResponse(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(responseContent));
    }
}
