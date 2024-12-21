package cz.cvut.fel.pm2.model;

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

