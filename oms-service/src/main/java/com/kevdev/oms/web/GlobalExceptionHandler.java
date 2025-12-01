package com.kevdev.oms.web;

import com.kevdev.oms.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError apiError = baseError(status, "Validation failed", request);

        ex.getBindingResult().getFieldErrors().forEach(error ->
                apiError.addFieldError(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = baseError(status, "Validation failed", request);

        ex.getConstraintViolations().forEach(cv ->
                apiError.addFieldError(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()
                )
        );

        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError apiError = baseError(status, ex.getMessage(), request);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String rootMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        ApiError apiError = baseError(status, rootMessage, request);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = baseError(status, ex.getMessage(), request);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";
        ApiError apiError = baseError(status, message, request);

        return new ResponseEntity<>(apiError, status);
    }

    private ApiError baseError(HttpStatus status, String message, HttpServletRequest request) {
        String path = request.getRequestURI();
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}
