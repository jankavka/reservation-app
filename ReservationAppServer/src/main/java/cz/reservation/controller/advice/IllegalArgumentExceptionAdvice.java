package cz.reservation.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class IllegalArgumentExceptionAdvice {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    ResponseEntity<Map<String, String>> handleException(RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Illegal argument: " + e.getMessage()));

    }
}
