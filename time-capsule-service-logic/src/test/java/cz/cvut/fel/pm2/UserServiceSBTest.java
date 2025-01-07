package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.MailService;
import cz.cvut.fel.pm2.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceSBTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Test
    void testGetCapsulesSuccess() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Assuming a mock implementation or additional logic
        // Replace with actual logic if different
        assertDoesNotThrow(() -> userService.getCapsules(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetCapsulesUserNotFound() {
        String email = "user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.getCapsules(email));
    }

    @Test
    void testRegisterUserSuccess() {
        String email = "newuser@example.com";
        String password = "securePassword";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User result = userService.registerUser(password, email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        String email = "existing@example.com";
        String password = "securePassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(password, email));
    }

    @Test
    void testLoginUserSuccess() throws IllegalAccessException {
        String email = "user@example.com";
        String password = "correctPassword";

        User user = new User();
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_REGISTERED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        Optional<User> result = userService.loginUser(email, password);

        assertTrue(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testLoginUserIncorrectPassword() throws IllegalAccessException {
        String email = "user@example.com";
        String password = "wrongPassword";

        User user = new User();
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_REGISTERED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        Optional<User> result = userService.loginUser(email, password);

        assertFalse(result.isPresent());
    }

    @Test
    void testLoginUserDeleted() {
        String email = "deleted@example.com";
        String password = "securePassword";

        User user = new User();
        user.setRole(Role.ROLE_DELETED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(Exception.class, () -> userService.loginUser(email, password));
    }


    @Test
    void testDeleteUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.deleteUser(email));
    }

    @Test
    void testChangePasswordSuccess() {
        String email = "user@example.com";
        String currentPassword = "currentPassword";
        String newPassword = "newSecurePassword";

        User user = new User();
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        assertDoesNotThrow(() -> userService.changePassword(email, currentPassword, newPassword));

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePasswordIncorrectCurrentPassword() {
        String email = "user@example.com";
        String currentPassword = "wrongPassword";
        String newPassword = "newSecurePassword";

        User user = new User();
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, "encodedCurrentPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(email, currentPassword, newPassword));
    }
}
