package cz.cvut.fel.pm2.exception;

public class UserDeletedException extends IllegalAccessException {
    public static final String DEFAULT_MESSAGE = "Tento účet byl smazán. Pro obnovení kontaktujte podporu.";

    public UserDeletedException() {
        super(DEFAULT_MESSAGE);
    }

    public UserDeletedException(String message) {
        super(message);
    }
}
