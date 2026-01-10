package cz.reservation.controller.advice;

import cz.reservation.service.exception.MissingPricingTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MissingPricingTypeExceptionAdvice {

    @ExceptionHandler(MissingPricingTypeException.class)
    public ResponseEntity<Map<String, String>> handleException(MissingPricingTypeException e) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", e.getMessage()));

    }
}
