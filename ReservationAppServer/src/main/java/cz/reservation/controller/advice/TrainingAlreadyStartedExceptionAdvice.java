package cz.reservation.controller.advice;

import cz.reservation.service.exception.TrainingAlreadyStartedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TrainingAlreadyStartedExceptionAdvice {

    @ExceptionHandler(TrainingAlreadyStartedException.class)
    public ResponseEntity<Map<String, String>> handleException(TrainingAlreadyStartedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }
}
