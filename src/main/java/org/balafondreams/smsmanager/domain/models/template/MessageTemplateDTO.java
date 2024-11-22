package org.balafondreams.smsmanager.domain.models.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class MessageTemplateDTO {
    private Long id;

    @NotBlank(message = "Template name is required")
    @Size(min = 2, max = 100, message = "Template name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Template content is required")
    @Size(max = 1000, message = "Template content cannot exceed 1000 characters")
    private String content;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private boolean isActive = true;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private Set<TemplateVariableDTO> variables;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
