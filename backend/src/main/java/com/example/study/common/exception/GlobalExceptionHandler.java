package com.example.study.common.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @RestControllerAdvice는 여러 컨트롤러에서 발생하는 예외를
 * 중앙에서 가로채 JSON 응답으로 변환한다.
 *
 * 왜 필요한가?
 * - Controller마다 try-catch를 반복하지 않아도 된다.
 * - 에러 응답 형식을 일관되게 유지할 수 있다.
 * - 프론트엔드는 일관된 구조의 에러 응답을 받게 된다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public org.springframework.http.ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException exception) {
        Map<String, Object> body = baseBody(HttpStatus.NOT_FOUND, exception.getMessage());
        return org.springframework.http.ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        body.put("fieldErrors", fieldErrors);
        return org.springframework.http.ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        Map<String, Object> body = baseBody(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> baseBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
