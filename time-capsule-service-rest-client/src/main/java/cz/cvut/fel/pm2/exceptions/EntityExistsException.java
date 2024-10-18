package cz.cvut.fel.pm2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
/**
 * Exception thrown when an entity already exists.
 */
public class EntityExistsException extends ResponseStatusException {
    public EntityExistsException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}