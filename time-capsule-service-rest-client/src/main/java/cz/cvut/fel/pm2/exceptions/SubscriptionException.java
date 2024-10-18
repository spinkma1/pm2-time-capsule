package cz.cvut.fel.pm2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
/**
 * Exception thrown when there is an issue with a subscription.
 */
public class SubscriptionException extends ResponseStatusException {
    public SubscriptionException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}