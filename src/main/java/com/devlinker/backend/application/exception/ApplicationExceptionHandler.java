package com.devlinker.backend.application.exception;

import com.devlinker.backend.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplicationException(ApplicationException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.fail(e.getCode(), e.getMessage()));
    }
}