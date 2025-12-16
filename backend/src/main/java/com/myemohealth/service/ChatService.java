package com.myemohealth.service;

import com.myemohealth.entity.ChatMessage;
import com.myemohealth.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

        private final ChatMessageRepository chatMessageRepository;
        private final SimpMessagingTemplate messagingTemplate;

        public ChatMessage saveAndSend(ChatMessage message) {
                // Save to DB
                ChatMessage saved = chatMessageRepository.save(message);

                // WebSocket sending removed - using REST API only
                // Messages will be loaded via polling or manual refresh

                return saved;
        }

        public List<ChatMessage> getConversation(Long userId1, Long userId2) {
                return chatMessageRepository.findConversation(userId1, userId2);
        }

        /**
         * Get list of users that the current user has conversations with
         */
        public List<Map<String, Object>> getUserConversations(Long userId) {
                // Get all messages where user is sender or recipient
                List<ChatMessage> allMessages = chatMessageRepository.findAll().stream()
                                .filter(m -> m.getSenderId().equals(userId) || m.getRecipientId().equals(userId))
                                .collect(Collectors.toList());

                // Extract unique conversation partner IDs
                return allMessages.stream()
                                .map(m -> m.getSenderId().equals(userId) ? m.getRecipientId() : m.getSenderId())
                                .distinct()
                                .map(partnerId -> {
                                        Map<String, Object> conversation = new java.util.HashMap<>();
                                        conversation.put("userId", partnerId);
                                        // Get last message with this partner
                                        ChatMessage lastMessage = allMessages.stream()
                                                        .filter(m -> (m.getSenderId().equals(userId)
                                                                        && m.getRecipientId().equals(partnerId)) ||
                                                                        (m.getSenderId().equals(partnerId)
                                                                                        && m.getRecipientId().equals(
                                                                                                        userId)))
                                                        .reduce((first, second) -> second)
                                                        .orElse(null);
                                        if (lastMessage != null) {
                                                conversation.put("lastMessage", lastMessage.getContent());
                                                conversation.put("timestamp", lastMessage.getTimestamp());
                                        }
                                        return conversation;
                                })
                                .collect(Collectors.toList());
        }

        /**
         * Send WebRTC signaling message (offer, answer, ICE candidate)
         */
        public void sendCallSignal(Long recipientId, String type, Map<String, Object> data) {
                messagingTemplate.convertAndSendToUser(
                                String.valueOf(recipientId),
                                "/queue/call-signal",
                                Map.of("type", type, "data", data));
        }
}
