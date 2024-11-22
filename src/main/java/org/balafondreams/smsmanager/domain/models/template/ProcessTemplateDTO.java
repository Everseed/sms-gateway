package org.balafondreams.smsmanager.domain.models.template;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ProcessTemplateDTO {
    @NotNull
    private Map<String, String> variables;
}