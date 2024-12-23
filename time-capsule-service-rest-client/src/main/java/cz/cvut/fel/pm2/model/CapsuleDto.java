package cz.cvut.fel.pm2.model;


import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object representing capsule.
 */
public record CapsuleDto(
        Long userId,

        String name,
        String description,

        Boolean teamWork,

        Long capsuleSize,

        LocalDateTime unlockTime,
        String qrCodePassword,

        Double unlockLat,
        Double unlockLongit,

        List<UserDto> users,

        UnlockMethodsDto unlockMethods,

        String state
) {}

