package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationPreviewDTO {
    private Long id;
    private String phoneNumber;
    private String name;
    private LocalDateTime lastMessageAt;
    private String lastMessageContent;
    private int unreadCount;
}
