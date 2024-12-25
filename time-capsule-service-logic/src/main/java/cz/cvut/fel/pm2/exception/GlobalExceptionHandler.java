package cz.cvut.fel.pm2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid JSON input: {}", ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid JSON input: " + ex.getLocalizedMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred: " + ex.getMessage()));
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<Map<String, String>> handleUserDeletionException(UserDeletedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tento účet byl smazán. Pro obnovení kontaktujte podporu.");
        response.put("error", "ACCOUNT_DELETED");

        // Vrátíme odpověď s HTTP statusem 202 (Accepted)
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
