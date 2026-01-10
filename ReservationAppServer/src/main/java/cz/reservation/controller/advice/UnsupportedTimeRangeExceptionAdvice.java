package cz.reservation.controller.advice;

import cz.reservation.service.exception.UnsupportedTimeRangeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class UnsupportedTimeRangeExceptionAdvice {

    public ResponseEntity<Map<String, String>> handleException(UnsupportedTimeRangeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", e.getMessage()));
    }
}
