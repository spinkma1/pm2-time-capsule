package cz.cvut.fel.pm2.model;


import java.util.List;
public record UserDto(
        Long id,
        String email,
        String name,
        String bio,
        String role,
        List<UserDto> followers,
        List<CapsuleDto> capsules
) {}