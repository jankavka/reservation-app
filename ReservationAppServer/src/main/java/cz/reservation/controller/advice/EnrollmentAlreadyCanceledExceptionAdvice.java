package cz.reservation.controller.advice;

import cz.reservation.service.exception.EnrollmentAlreadyCanceledException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class EnrollmentAlreadyCanceledExceptionAdvice {

    @ExceptionHandler(EnrollmentAlreadyCanceledException.class)
    ResponseEntity<Map<String, String>> handleException(EnrollmentAlreadyCanceledException e ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
