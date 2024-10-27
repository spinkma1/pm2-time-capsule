package cz.cvut.fel.pm2.model;


import java.util.List;

/**
 * Data transfer object representing a discount.
 */
public record CapsuleDto(

        Long id,

        String name,

        String description,

        Boolean teamWork,

        Long userFileLimit,

        List<UserDto> users
) {

}
