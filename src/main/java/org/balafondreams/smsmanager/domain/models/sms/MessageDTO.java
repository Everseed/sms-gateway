package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private String phoneNumber;
    private String status;
    private String type;
    private Long userId;
    private Long conversationId;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
}
