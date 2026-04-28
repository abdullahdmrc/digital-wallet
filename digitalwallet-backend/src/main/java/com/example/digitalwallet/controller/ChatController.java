package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.ChatRequest;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.service.ChatAssistant;
import com.example.digitalwallet.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatAssistant chatAssistant;
    private final ChatService chatService;


    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
       return ResponseEntity.ok(chatService.chat(request));
    }
}
