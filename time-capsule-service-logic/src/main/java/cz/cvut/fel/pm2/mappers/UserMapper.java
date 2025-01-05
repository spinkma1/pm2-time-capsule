package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CapsuleMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user the user entity to convert
     * @return the converted user data transfer object
     */
    @Mapping(source = "followers", target = "followers")
    @Mapping(source = "capsules", target = "capsules")
    UserDto toDto(User user);

    /**
     * Converts a list of User entities to a list of UserDto.
     *
     * @param users the list of user entities to convert
     * @return the list of converted user data transfer objects
     */
    List<UserDto> toDtoList(List<User> users);

    /**
     * Converts a UserDto to a User entity.
     *
     * @param userDto the user data transfer object to convert
     * @return the converted user entity
     */
    @Mapping(source = "followers", target = "followers")
    @Mapping(source = "capsules", target = "capsules")
    User toEntity(UserDto userDto);

}