package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Convert User entity to UserDto
    @Mapping(source = "followers", target = "followers")
    @Mapping(source = "capsules", target = "capsules")
    UserDto toDto(User user);

    // Convert list of User entities to list of UserDto records
    List<UserDto> toDtoList(List<User> users);

    // Convert UserDto to User entity
    @Mapping(source = "followers", target = "followers")
    @Mapping(source = "capsules", target = "capsules")
    User toEntity(UserDto userDto);
}
