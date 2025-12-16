package com.myemohealth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long recipientId;

    @Column(columnDefinition = "TEXT")
    private String content;

    // TEXT, IMAGE, VIDEO_CALL_START, VIDEO_CALL_END
    private String type;

    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private boolean isRead;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (!isRead) {
            isRead = false;
        }
    }
}
