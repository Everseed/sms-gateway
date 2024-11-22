package org.balafondreams.smsmanager.domain.models.template;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    private Set<String> detectedVariables;

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.valid = false;
    }

    public void addDetectedVariable(String variable) {
        if (this.detectedVariables == null) {
            this.detectedVariables = new HashSet<>();
        }
        this.detectedVariables.add(variable);
    }
}
