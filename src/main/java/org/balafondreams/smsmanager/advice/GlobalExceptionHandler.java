package org.balafondreams.smsmanager.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.InvalidRoleException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.exception.SMSGatewayException;
import org.balafondreams.smsmanager.domain.models.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code("RESOURCE_NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code("DUPLICATE_RESOURCE")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(
            InvalidRoleException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code("INVALID_ROLE")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SMSGatewayException.class)
    public ResponseEntity<ErrorResponse> handleSMSGatewayException(
            SMSGatewayException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code("SMS_GATEWAY_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ErrorResponse error = ErrorResponse.builder()
                .message("Validation failed")
                .code("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message("Access denied")
                .code("ACCESS_DENIED")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .code("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}