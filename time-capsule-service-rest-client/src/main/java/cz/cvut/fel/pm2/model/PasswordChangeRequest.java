package cz.cvut.fel.pm2.model;

public record PasswordChangeRequest(
        String currentPassword,
        String newPassword
) {}
