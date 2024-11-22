package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ContactDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private Long userId;
    private Set<GroupDTO> groups;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
