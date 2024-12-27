package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.exception.UserDeletedException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapperImp;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public List<Capsule> getCapsules(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getCapsules();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<String> getFollowers(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getFollowers().stream().map(User::getEmail).toList();
    }

    @Transactional
    public void findOrCreateUser(OidcUser oidcUser) {
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        // Check if a user with this Google ID already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            existingUser.get();
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
        newUser.setRole(Role.REGISTERED);
        userRepository.save(newUser);
    }

    public User registerUser(String password, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(Role.REGISTERED);
        user.setCapsules(List.of());
        user.setFollowers(List.of());
        userRepository.save(user);
        return user;
    }

    public Optional<User> loginUser(String username, String password) throws IllegalAccessException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            if (user.get().getRole() == Role.DELETED) {
                throw new UserDeletedException("Tento účet byl smazán. Pro obnovení kontaktujte podporu.");            }
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), new ArrayList<>());
    }



    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Místo fyzického smazání jen změníme roli
        user.setRole(Role.DELETED);
        userRepository.save(user);

        // Poslat email o smazání účtu
        // TODO
        /*
        mailService.sendEmail(
                user.getEmail(),
                "Účet byl smazán",
                "Váš účet byl úspěšně smazán. Pokud budete chtít účet obnovit, kontaktujte podporu."
        );
         */
    }

    @Transactional
    public void updateProfile(String email, Map<String, String> updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
