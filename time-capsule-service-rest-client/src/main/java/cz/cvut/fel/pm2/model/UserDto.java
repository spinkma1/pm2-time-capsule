package cz.cvut.fel.pm2.model;


import java.util.List;
/**
 * Data transfer object representing a user.
 *
 * @param id the unique identifier of the user
 * @param email the email address of the user
 * @param name the name of the user
 * @param bio the biography of the user
 * @param role the role of the user
 * @param followers the list of followers of the user
 * @param capsules the list of capsules associated with the user
 */
public record UserDto(
        Long id,
        String email,
        String name,
        String bio,
        String role,
        List<UserDto> followers,
        List<CapsuleDto> capsules
) {}