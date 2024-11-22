package org.balafondreams.smsmanager.domain.exception;

public class SMSGatewayException extends RuntimeException {
    public SMSGatewayException(String message) {
        super(message);
    }

    public SMSGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}