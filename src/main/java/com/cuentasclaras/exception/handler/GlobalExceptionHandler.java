package com.cuentasclaras.exception.handler;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        message.isBlank() ? "Datos de entrada inválidos" : message,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Error interno no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of(
                        500,
                        "Error interno no controlado",
                        null
                ));
    }
}
