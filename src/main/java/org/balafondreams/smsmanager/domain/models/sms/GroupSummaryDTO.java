package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private int contactCount;
    private LocalDateTime createdAt;
}