package cz.cvut.fel.pm2.model;




import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object representing capsule.
 */
public record CapsuleDto(
        Long id,
        UserDto owner,

        String name,
        String description,

        Boolean teamWork,

        Long capsuleSize,

        String unlockTime,
        String qrCodePassword,

        Double unlockLat,
        Double unlockLongit,

        List<UserDto> users,

        UnlockMethodsDto unlockMethods,

        String state,

        List<ContentDto> content
) {}

