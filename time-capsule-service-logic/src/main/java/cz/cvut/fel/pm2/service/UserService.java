package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.exception.UserDeletedException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapperImp;
import cz.cvut.fel.pm2.mappers.UserMapperImpl;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CapsuleMapperImp capsuleMapperImp;
    private final MailService mailService;
    private final UserMapperImpl userMapperImpl;

    private static final String NOT_FOUND_USER_MESSAGE = "User not found";

    /**
     * Retrieves the list of capsules associated with the user identified by the given email.
     *
     * @param email the email of the user
     * @return the list of capsules associated with the user
     * @throws IllegalArgumentException if the user is not found
     */
    public List<Capsule> getCapsules(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE))
                .getCapsules();
    }

    /**
     * Retrieves the user details for an admin user identified by the given email.
     *
     * @param email the email of the admin user
     * @return the user details as a UserDto
     * @throws NotFoundException if the user is not found
     */
    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public UserDto getAdminUser(String email) {
        return userMapperImpl
                .toDto(userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new NotFoundException(NOT_FOUND_USER_MESSAGE)));
    }

    /**
     * Retrieves the list of followers' emails for the user identified by the given email.
     *
     * @param email the email of the user
     * @return the list of followers' emails
     * @throws IllegalArgumentException if the user is not found
     */
    public List<String> getFollowers(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE))
                .getFollowers().stream().map(User::getEmail).toList();
    }

    /**
     * Finds or creates a user based on the given OIDC user information.
     *
     * @param oidcUser the OIDC user information
     */
    @Transactional
    public void findOrCreateUser(OidcUser oidcUser) {
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return;
        }

        existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setGoogleId(googleId);
            userRepository.save(user);
            return;
        }

        User newUser = new User(email, googleId);
        newUser.setRole(Role.ROLE_REGISTERED);
        mailService.sendEmail(email,"Time capsule registration", "Vítej v aplikaci Time Capsule! Děkujeme ti za registraci.");
        userRepository.save(newUser);
    }

    /**
     * Registers a new user with the given password and email.
     *
     * @param password the password of the new user
     * @param email the email of the new user
     * @return the registered user
     * @throws IllegalArgumentException if the user already exists
     */
    public User registerUser(String password, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(Role.ROLE_REGISTERED);
        user.setCapsules(List.of());
        user.setFollowers(List.of());
        userRepository.save(user);
        mailService.sendEmail(email,"Time capsule registration", "Vítej v aplikaci Time Capsule! Děkujeme ti za registraci.");
        return user;
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an Optional containing the user if login is successful, or an empty Optional if login fails
     * @throws IllegalAccessException if the user account is deleted or banned
     */
    public Optional<User> loginUser(String username, String password) throws IllegalAccessException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            if (user.get().getRole() == Role.ROLE_DELETED || user.get().getRole() == Role.ROLE_BANNED) {
                throw new UserDeletedException("Tento účet byl smazán nebo zabanován. Pro obnovení kontaktujte podporu.");
            }
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    /**
     * Loads the user details for the user identified by the given email.
     *
     * @param email the email of the user
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER_MESSAGE));

        List<GrantedAuthority> authorities = List.of(() -> user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * Deletes the user identified by the given email.
     *
     * @param email the email of the user to delete
     * @throws IllegalArgumentException if the user is not found
     */
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        user.setRole(Role.ROLE_DELETED);
        userRepository.save(user);

        mailService.sendEmail(
                user.getEmail(),
                "Účet byl smazán",
                "Váš účet byl úspěšně smazán. Pokud budete chtít účet obnovit, kontaktujte podporu."
        );
    }

    /**
     * Updates the profile of the user identified by the given email with the provided updates.
     *
     * @param email the email of the user
     * @param updates a map containing the updates to apply to the user's profile
     * @throws IllegalArgumentException if the user is not found or if the password is incorrect
     */
    @Transactional
    public void updateProfile(String email, Map<String, String> updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }

        if (updates.containsKey("bio")) {
            user.setBio(updates.get("bio"));
        }

        if (updates.containsKey("email")) {
            if (!passwordEncoder.matches(updates.get("password"), user.getPassword())) {
                throw new IllegalArgumentException("Password is incorrect");
            }

            handleEmailUpdate(user, updates.get("email"));
        }

        User savedUser = userRepository.save(user);
        convertToDto(savedUser);
    }

    /**
     * Finds emails based on the given query.
     *
     * @param query the search query
     * @return a list of emails matching the query
     */
    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public List<String> findEmails(String query) {
        return userRepository.findByEmailContaining(query)
                .map(users -> users.stream()
                        .map(User::getEmail)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    /**
     * Updates the user details based on the provided user data transfer object.
     *
     * @param userDto a map containing the user details to update
     * @return true if the update was successful, false otherwise
     * @throws NotFoundException if the user is not found
     */
    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public Boolean updateUser(Map<String, String> userDto) {
        User user = userRepository.findByEmail(userDto.get("email"))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));

        user.setName(userDto.get("name"));
        user.setBio(userDto.get("bio"));
        user.setRole(Role.valueOf(userDto.get("role")));

        userRepository.save(user);
        return true;
    }

    /**
     * Handles the email update for the given user.
     *
     * @param user the user whose email is to be updated
     * @param newEmail the new email to set
     * @throws IllegalArgumentException if the new email is already in use
     */
    private void handleEmailUpdate(User user, String newEmail) {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.setEmail(newEmail);
    }

    /**
     * Retrieves the user profile for the user identified by the given email.
     *
     * @param email the email of the user
     * @return the user profile
     * @throws NotFoundException if the user is not found
     */
    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));
    }

    /**
     * Changes the password for the user identified by the given email.
     *
     * @param email the email of the user
     * @param currentPassword the current password of the user
     * @param newPassword the new password to set
     * @throws IllegalArgumentException if the user is not found, if the current password is incorrect, or if the new password is too short
     */
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Converts the given user entity to a UserDto.
     *
     * @param user the user entity to convert
     * @return the converted UserDto
     */
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId().longValue(),
                user.getEmail(),
                user.getName(),
                user.getBio(),
                user.getRole().toString(),
                user.getFollowers().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()),
                user.getCapsules().stream()
                        .map(capsuleMapperImp::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Searches for users based on the given query.
     *
     * @param query the search query
     * @return a list of UserDto objects matching the query
     */
    public List<UserDto> searchUsers(String query) {
        String lowercaseQuery = query.toLowerCase();
        return userRepository.findByEmailContainingOrNameContaining(lowercaseQuery, lowercaseQuery)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
