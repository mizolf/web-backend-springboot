package com.mcesnik.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "File size exceeds the maximum limit of 10MB"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
        String message = e.getMessage();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (message != null) {
            if (message.contains("Access denied")) status = HttpStatus.FORBIDDEN;
            else if (message.contains("not found")) status = HttpStatus.NOT_FOUND;
            else if (message.contains("empty") || message.contains("must be") || message.contains("File must be"))
                status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status)
                .body(Map.of("error", message != null ? message : "An unexpected error occurred"));
    }
}
