package org.balafondreams.smsmanager.domain.models.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;
}
