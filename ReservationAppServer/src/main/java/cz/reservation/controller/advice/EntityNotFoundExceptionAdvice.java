package cz.reservation.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class EntityNotFoundExceptionAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<Map<String, String>> handleException(EntityNotFoundException e ){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Internal Server Error: " + e.getMessage()));
    }
}
