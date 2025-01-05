package cz.cvut.fel.pm2.enums;


/**
 * Enumeration representing the different methods to unlock a capsule.
 */
public enum UnlockMethod {
    /**
     * Unlock using a password.
     */
    PASSWORD,

    /**
     * Unlock using geolocation.
     */
    GEOLOCATION,

    /**
     * Unlock based on time.
     */
    TIME,

    /**
     * Unlock using a QR code.
     */
    QR_CODE
}