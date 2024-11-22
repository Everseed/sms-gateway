package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDTO {
    private Long id;
    private String phoneNumber;
    private String name;
    private LocalDateTime lastMessageAt;
    private String lastMessageContent;
    private int unreadCount;
    private List<MessageDTO> recentMessages;
}