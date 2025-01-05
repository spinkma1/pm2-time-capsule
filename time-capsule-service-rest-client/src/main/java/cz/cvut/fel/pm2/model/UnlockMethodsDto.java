package cz.cvut.fel.pm2.model;

/**
 * Data transfer object representing the unlock methods for a capsule.
 *
 * @param timeEnabled indicates if the time-based unlock method is enabled
 * @param timeComplete indicates if the time-based unlock method is complete
 * @param qrCodeEnabled indicates if the QR code-based unlock method is enabled
 * @param qrCodeComplete indicates if the QR code-based unlock method is complete
 * @param geolocationEnabled indicates if the geolocation-based unlock method is enabled
 * @param geolocationComplete indicates if the geolocation-based unlock method is complete
 * @param passwordEnabled indicates if the password-based unlock method is enabled
 * @param passwordComplete indicates if the password-based unlock method is complete
 */
public record UnlockMethodsDto(
        boolean timeEnabled,
        boolean timeComplete,

        boolean qrCodeEnabled,
        boolean qrCodeComplete,

        boolean geolocationEnabled,
        boolean geolocationComplete,

        boolean passwordEnabled,
        boolean passwordComplete
) {}

