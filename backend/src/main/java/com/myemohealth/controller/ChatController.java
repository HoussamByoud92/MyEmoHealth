package com.myemohealth.controller;

import com.myemohealth.entity.ChatMessage;
import com.myemohealth.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final com.myemohealth.repository.UserRepository userRepository;

    // WebSocket Endpoint: /app/chat
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatService.saveAndSend(message);
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        try {
            message.setTimestamp(LocalDateTime.now());
            ChatMessage saved = chatService.saveAndSend(message);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // REST Endpoint: Get History
    @GetMapping("/history/{userId1}/{userId2}")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {

        try {
            // Security check: Current user must be one of the participants
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            String currentEmail = auth.getName();

            // Allow participants and admins to view conversations
            java.util.Optional<com.myemohealth.entity.User> currentUserOpt = userRepository.findByEmail(currentEmail);
            boolean isParticipant = currentUserOpt
                    .map(u -> u.getId().equals(userId1) || u.getId().equals(userId2))
                    .orElse(false);

            boolean isAdmin = currentUserOpt
                    .map(u -> u.getRole() != null && "ADMIN".equals(u.getRole().getName()))
                    .orElse(false);

            if (!isParticipant && !isAdmin) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(chatService.getConversation(userId1, userId2));
        } catch (Exception e) {
            // Log the error and return empty list instead of 500
            System.err.println(
                    "Error fetching chat history for users " + userId1 + " and " + userId2 + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
    }

    // REST Endpoint: Get all conversations for current user
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        String currentEmail = auth.getName();
        java.util.Optional<com.myemohealth.entity.User> currentUserOpt = userRepository.findByEmail(currentEmail);

        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(chatService.getUserConversations(currentUserOpt.get().getId()));
    }

    // WebRTC Signaling Endpoints
    @MessageMapping("/call/offer")
    public void handleCallOffer(@Payload Map<String, Object> offer) {
        Long recipientId = Long.valueOf(offer.get("recipientId").toString());
        chatService.sendCallSignal(recipientId, "offer", offer);
    }

    @MessageMapping("/call/answer")
    public void handleCallAnswer(@Payload Map<String, Object> answer) {
        Long recipientId = Long.valueOf(answer.get("recipientId").toString());
        chatService.sendCallSignal(recipientId, "answer", answer);
    }

    @MessageMapping("/call/ice-candidate")
    public void handleIceCandidate(@Payload Map<String, Object> candidate) {
        Long recipientId = Long.valueOf(candidate.get("recipientId").toString());
        chatService.sendCallSignal(recipientId, "ice-candidate", candidate);
    }
}
