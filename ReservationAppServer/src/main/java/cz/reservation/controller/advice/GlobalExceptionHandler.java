package cz.reservation.controller.advice;

import cz.reservation.service.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MESSAGE_KEY = "message";

    // 400 Bad Request
    @ExceptionHandler({
            TrainingAlreadyStartedException.class,
            LateBookingCancelingException.class,
            EnrollmentAlreadyCanceledException.class,
            MissingPricingTypeException.class,
            HttpMessageNotReadableException.class,
            SQLException.class,
            EnrollmentNoActiveException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgument(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, "Illegal argument: " + e.getMessage()));
    }

    // 404 Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    // 409 Conflict
    @ExceptionHandler(UnsupportedTimeRangeException.class)
    public ResponseEntity<Map<String, String>> handleConflict(UnsupportedTimeRangeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    // 500 Internal Server Error
    @ExceptionHandler(InvoiceStorageException.class)
    public ResponseEntity<Map<String, String>> handleInternalError(InvoiceStorageException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    // 200 OK (for empty lists)
    @ExceptionHandler(EmptyListException.class)
    public ResponseEntity<Map<String, String>> handleEmptyList(EmptyListException e) {
        return ResponseEntity
                .ok(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    // Validation errors - return field map -> error message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(UnknownAlgorithmException.class)
    public ResponseEntity<Map<String, String>> handleUnknownAlgorithm(UnknownAlgorithmException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

    // 404 no static handler
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleUrlNotFound(NoResourceFoundException e, HttpServletRequest req) {
        String str = req.getRequestURL().toString();
        log.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(MESSAGE_KEY, "No content found with request on " + str));
    }

    @ExceptionHandler(CustomJsonException.class)
    public ResponseEntity<Map<String, String>> handleJsonException(CustomJsonException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }

}
