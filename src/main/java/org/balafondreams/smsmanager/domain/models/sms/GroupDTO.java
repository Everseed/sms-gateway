package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class GroupDTO {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private int contactCount;
    private Set<ContactSummaryDTO> contacts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
