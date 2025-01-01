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

    public List<Capsule> getCapsules(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE))
                .getCapsules();
    }


    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public UserDto getAdminUser(String email) {
        return userMapperImpl
                .toDto(userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new NotFoundException(NOT_FOUND_USER_MESSAGE)));
    }

    public List<String> getFollowers(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE))
                .getFollowers().stream().map(User::getEmail).toList();
    }

    @Transactional
    public void findOrCreateUser(OidcUser oidcUser) {
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        // Check if a user with this Google ID already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return;
        }

        // Check if a user with this email already exists
        existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Link this account to Google SSO if it was registered previously without Google
            user.setGoogleId(googleId);
            userRepository.save(user);
            return;
        }

        User newUser = new User(email, googleId);
        newUser.setRole(Role.ROLE_REGISTERED);
        mailService.sendEmail(email,"Time capsule registration", "Vítej v aplikaci Time Capsule! Děkujeme ti za registraci.");
        userRepository.save(newUser);
    }

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

    public Optional<User> loginUser(String username, String password) throws IllegalAccessException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            if (user.get().getRole() == Role.ROLE_DELETED || user.get().getRole() == Role.ROLE_BANNED) {
                throw new UserDeletedException("Tento účet byl smazán nebo zabanován. Pro obnovení kontaktujte podporu.");            }
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER_MESSAGE));

        // Create a collection of GrantedAuthority
        List<GrantedAuthority> authorities = List.of(() -> user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }




    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        // Místo fyzického smazání jen změníme roli
        user.setRole(Role.ROLE_DELETED);
        userRepository.save(user);

        mailService.sendEmail(
                user.getEmail(),
                "Účet byl smazán",
                "Váš účet byl úspěšně smazán. Pokud budete chtít účet obnovit, kontaktujte podporu."
        );
    }

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

        // Handle email update separately as it requires verification
        if (updates.containsKey("email")) {
            if (!passwordEncoder.matches(updates.get("password"), user.getPassword())) {
                throw new IllegalArgumentException("Password is incorrect");
            }

            // Here you would typically:
            // 1. Validate that the new email isn't already in use
            // 2. Send verification email
            // 3. Only update after verification
            handleEmailUpdate(user, updates.get("email"));
        }

        User savedUser = userRepository.save(user);
        convertToDto(savedUser);
    }

    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public List<String> findEmails(String query) {
        return userRepository.findByEmailContaining(query)
                .map(users -> users.stream()
                        .map(User::getEmail)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

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

    private void handleEmailUpdate(User user, String newEmail) {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Here you would typically generate a verification token and send an email
        // For now, we'll just update directly
        user.setEmail(newEmail);
    }

    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_MESSAGE));

        // Ověříme současné heslo
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password (můžete přidat vlastní validační pravidla)
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Zakódujeme a uložíme nové heslo
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

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

    public List<UserDto> searchUsers(String query) {
        String lowercaseQuery = query.toLowerCase();
        return userRepository.findByEmailContainingOrNameContaining(lowercaseQuery, lowercaseQuery)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
