package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void registerUser(String password, String email) {
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
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
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

        // Remove user from all capsules
        user.getCapsules().forEach(capsule -> {
            capsule.getUsers().remove(user);
        });

        // Remove user from followers/following relationships
        user.getFollowers().forEach(follower -> {
            follower.getFollowers().remove(user);
        });

        // Delete user
        userRepository.delete(user);
    }

    @Transactional
    public UserDto updateProfile(String email, Map<String, String> updates) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updates.containsKey("name")) {
            user.setEmail(updates.get("name"));
        }

        if (updates.containsKey("bio")) {
            user.setBio(updates.get("bio"));
        }

        // Handle email update separately as it requires verification
        if (updates.containsKey("newEmail")) {
            // Here you would typically:
            // 1. Validate that the new email isn't already in use
            // 2. Send verification email
            // 3. Only update after verification
            handleEmailUpdate(user, updates.get("newEmail"));
        }

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    private void handleEmailUpdate(User user, String newEmail) {
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Here you would typically generate a verification token and send an email
        // For now, we'll just update directly
        user.setEmail(newEmail);
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
                user.getRole().toString(),
                user.getFollowers().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()),
                user.getCapsules().stream()
                        .map(capsule -> new CapsuleDto(/* map capsule fields */))
                        .collect(Collectors.toList())
        );
    }
}
