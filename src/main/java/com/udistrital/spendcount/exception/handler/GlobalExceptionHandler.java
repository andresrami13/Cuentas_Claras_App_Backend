package com.udistrital.spendcount.exception.handler;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.of(
                        ex.getStatus().value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Object>> handleSystemException(SystemException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.of(
                        ex.getStatus().value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of(
                        500,
                        "Error interno no controlado",
                        null
                ));
    }
}