package org.balafondreams.smsmanager.domain.exception;

import lombok.Getter;
import java.util.Map;

@Getter
public class InvalidRequestException extends RuntimeException {
    private final Map<String, String> errors;

    public InvalidRequestException(String message) {
        super(message);
        this.errors = null;
    }

    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}