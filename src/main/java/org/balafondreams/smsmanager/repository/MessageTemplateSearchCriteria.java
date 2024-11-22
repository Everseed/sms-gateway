package org.balafondreams.smsmanager.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageTemplateSearchCriteria {
    private Long userId;
    private Long categoryId;
    private String query;
    private String variableType;
    private boolean includeInactive;

    // Pour la pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}