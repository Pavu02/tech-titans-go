package com.examly.springapp.exceptions;

import com.examly.springapp.model.ErrorLog;
import com.examly.springapp.repository.ErrorLogRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired(required = false)
    private ErrorLogRepo errorLogRepo;

    private void logException(HttpServletRequest request, Exception ex) {
        try {
            if (errorLogRepo != null && request != null) {
                ErrorLog log = new ErrorLog(
                        request.getRequestURI(),
                        request.getMethod(),
                        ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName(),
                        LocalDateTime.now()
                );
                errorLogRepo.save(log);
            }
        } catch (Exception ignored) {
        }
    }

    @ExceptionHandler(BookException.class)
    public ResponseEntity<Map<String, Object>> handleBookException(BookException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BookDeletionException.class)
    public ResponseEntity<Map<String, Object>> handleBookDeletionException(BookDeletionException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DuplicateBookException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateBookException(DuplicateBookException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid Email or Password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Access Denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllOtherExceptions(Exception ex, HttpServletRequest request) {
        logException(request, ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "Something Went Wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
