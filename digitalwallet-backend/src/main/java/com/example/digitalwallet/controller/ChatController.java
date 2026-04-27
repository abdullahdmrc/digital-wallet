package com.example.digitalwallet.controller;

import com.example.digitalwallet.dto.ChatRequest;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.service.ChatAssistant;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatAssistant chatAssistant;

    public ChatController(ChatAssistant chatAssistant) {
        this.chatAssistant = chatAssistant;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Lütfen giriş yapın.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            int customerId = user.getId();
            
            String response = chatAssistant.chat(customerId, request.getMessage());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(403).body("Kullanıcı doğrulanamadı.");
    }
}
