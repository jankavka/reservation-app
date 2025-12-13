package cz.reservation.controller.advice;

import cz.reservation.service.exception.LateBookingCancelingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class LateBookingCancelingExceptionAdvice {

    @ExceptionHandler(LateBookingCancelingException.class)
    public ResponseEntity<Map<String, String>> handleException(LateBookingCancelingException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }
}
