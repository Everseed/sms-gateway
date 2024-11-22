package org.balafondreams.smsmanager.domain.exception;

public class InvalidTemplateException extends RuntimeException {
    public InvalidTemplateException(String message) {
        super(message);
    }
}