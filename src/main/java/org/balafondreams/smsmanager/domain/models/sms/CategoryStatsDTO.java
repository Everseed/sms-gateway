package org.balafondreams.smsmanager.domain.models.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CategoryStatsDTO {
    private Long id;
    private String name;
    private Long templateCount;
    private LocalDateTime lastTemplateCreated;
    private LocalDateTime firstTemplateCreated;
}