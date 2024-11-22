package org.balafondreams.smsmanager.domain.models.template;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TemplateUsageDTO {
    private Long id;
    private String processedContent;
    private Map<String, String> variablesUsed;
    private LocalDateTime usedAt;
    private String usedBy;
}
