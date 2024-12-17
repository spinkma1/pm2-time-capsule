package cz.cvut.fel.pm2.model;


import java.util.List;

/**
 * Data transfer object representing capsule.
 */
public record CapsuleDto(

        Long id,
        Long userId,

        String name,

        String description,

        Boolean teamWork,

        Long userFileLimit,

        List<UserDto> users
) {

}
