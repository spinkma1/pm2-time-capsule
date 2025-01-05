package cz.cvut.fel.pm2.exception;

/**
 * Exception thrown when a user account has been deleted.
 */
public class UserDeletedException extends IllegalAccessException {
    /**
     * Default message for the exception.
     */
    public static final String DEFAULT_MESSAGE = "Tento účet byl smazán. Pro obnovení kontaktujte podporu.";

    /**
     * Constructs a new UserDeletedException with the default message.
     */
    public UserDeletedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new UserDeletedException with a specified message.
     *
     * @param message the detail message
     */
    public UserDeletedException(String message) {
        super(message);
    }
}