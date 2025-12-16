package com.myemohealth.repository;

import com.myemohealth.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    // Find conversation between two users (bidirectional)
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
