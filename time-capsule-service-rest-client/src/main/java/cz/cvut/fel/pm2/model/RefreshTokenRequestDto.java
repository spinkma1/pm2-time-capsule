package cz.cvut.fel.pm2.model;
import lombok.Data;

/**
 * Data transfer object for refresh token requests.
 */
@Data
public class RefreshTokenRequestDto {
    /**
     * The refresh token.
     */
    private String refreshToken;
}