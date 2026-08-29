package com.booking.resource_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
    @ExceptionHandler(IllegalStateException.class)
public ResponseEntity<Map<String, String>> handleConflict(
        IllegalStateException ex) {

    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
}

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, String> error = new HashMap<>();

        error.put("error", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage() != null &&
                (ex.getMessage().contains("not found"))) {

            status = HttpStatus.NOT_FOUND;
        }

        if (ex.getMessage() != null &&
                ex.getMessage().contains("not authorized")) {

            status = HttpStatus.FORBIDDEN;
        }

        return ResponseEntity
                .status(status)
                .body(error);
    }
}