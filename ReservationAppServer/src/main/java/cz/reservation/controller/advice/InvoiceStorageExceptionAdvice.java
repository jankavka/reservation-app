package cz.reservation.controller.advice;

import cz.reservation.service.exception.InvoiceStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class InvoiceStorageExceptionAdvice {

    @ExceptionHandler(InvoiceStorageException.class)
    public ResponseEntity<Map<String,String>> handleException(InvoiceStorageException e ){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }
}
