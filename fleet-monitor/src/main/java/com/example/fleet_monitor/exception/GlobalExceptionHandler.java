package com.example.fleet_monitor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleDeviceNotFound(
            DeviceNotFoundException exception
    ) {
        return Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", exception.getMessage()
        );
    }

    @ExceptionHandler(DeviceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDeviceAlreadyExists(
            DeviceAlreadyExistsException exception
    ) {
        return Map.of(
                "timestamp", Instant.now(),
                "status", 409,
                "error", exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request");

        return Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", message
        );
    }
}
