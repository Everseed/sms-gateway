package org.balafondreams.smsmanager.domain.models.template;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class MessageTemplateUpdateDTO {
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 1000)
    private String content;

    @Size(max = 255)
    private String description;

    private Long categoryId;

    @Valid
    private Set<TemplateVariableDTO> variables;
}
