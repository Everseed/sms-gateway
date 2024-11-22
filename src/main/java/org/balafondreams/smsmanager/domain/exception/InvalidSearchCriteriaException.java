package org.balafondreams.smsmanager.domain.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class InvalidSearchCriteriaException extends RuntimeException {
    private final List<String> errors;

    public InvalidSearchCriteriaException(List<String> errors) {
        super("Invalid search criteria: " + String.join(", ", errors));
        this.errors = errors;
    }
}
