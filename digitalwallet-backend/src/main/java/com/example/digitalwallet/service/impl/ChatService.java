package com.example.digitalwallet.service.impl;

import com.example.digitalwallet.dto.ChatRequest;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.service.ChatAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatAssistant chatAssistant;

    public String chat(ChatRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ("Lütfen giriş yapın.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            int customerId = user.getId();
            return chatAssistant.chat(customerId, request.getMessage());

        }
        return ("Kullanıcı doğrulanamadı");
    }
}
