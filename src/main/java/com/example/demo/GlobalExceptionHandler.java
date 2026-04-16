package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(RuntimeException.class)
    // public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //             .body(Map.of("error", ex.getMessage()));
    // }

    // @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    // public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //             .body(Map.of("error", ex.getMessage()));
    // }

    // @ExceptionHandler(HttpMessageNotReadableException.class)
    // public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //             .body(Map.of("error", ex.getMessage()));
    // }

    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //             .body(Map.of("error", ex.getMessage()));
    // }

    // @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    // public ResponseEntity<Map<String, String>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    //     return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
    //             .body(Map.of("error", ex.getMessage()));
    // }
}
