package cz.reservation.controller.advice;

import cz.reservation.service.exception.EmptyListException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class EmptyListExceptionAdvice {

    @ExceptionHandler(EmptyListException.class)
    public ResponseEntity<Map<String, String>> handleEmptyListException(EmptyListException e ){
        return ResponseEntity.ok(Map.of("message", e.getMessage()));

    }
}
