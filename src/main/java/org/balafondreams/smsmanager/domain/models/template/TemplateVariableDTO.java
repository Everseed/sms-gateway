package org.balafondreams.smsmanager.domain.models.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class TemplateVariableDTO {
    private Long id;

    @NotBlank(message = "Variable key is required")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "Variable key must start with a letter and contain only letters, numbers, and underscores")
    private String key;

    private String defaultValue;

    @NotBlank(message = "Variable type is required")
    private String type;

    private String description;
    private Long templateId;
}