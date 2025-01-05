package cz.cvut.fel.pm2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling various exceptions across the application.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles HttpMessageNotReadableException, which occurs when the JSON input is invalid.
     *
     * @param ex the exception thrown when the JSON input is not readable
     * @return a ResponseEntity containing a message about the invalid JSON input
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid JSON input: {}", ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid JSON input: " + ex.getLocalizedMessage()));
    }

    /**
     * Handles general exceptions that are not specifically handled by other methods.
     *
     * @param ex the exception thrown
     * @return a ResponseEntity containing a message about the unexpected error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred: " + ex.getMessage()));
    }

    /**
     * Handles UserDeletedException, which occurs when a user account has been deleted.
     *
     * @param ex the exception thrown when the user account is deleted
     * @return a ResponseEntity containing a message about the account deletion and an error code
     */
    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<Map<String, String>> handleUserDeletionException(UserDeletedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tento účet byl smazán. Pro obnovení kontaktujte podporu.");
        response.put("error", "ACCOUNT_DELETED");

        // Return a response with HTTP status 202 (Accepted)
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}