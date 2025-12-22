package cz.reservation.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class SQLExceptionAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String,String>> handleException(SQLException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }
}
