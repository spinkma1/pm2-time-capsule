package cz.cvut.fel.pm2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
/**
 * Exception thrown when the body of a request is invalid.
 */
public class InvalidBodyException extends ResponseStatusException {
    public InvalidBodyException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);

    }
}
