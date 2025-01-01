package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.exception.UserDeletedException;
import cz.cvut.fel.pm2.mappers.CapsuleMapperImp;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CapsuleMapperImp capsuleMapperImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCapsulesSuccess() {
        String email = "test@example.com";
        Capsule capsule = new Capsule();
        List<Capsule> capsules = List.of(capsule);

        User user = new User();
        user.setCapsules(capsules);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<Capsule> result = userService.getCapsules(email);

        assertNotNull(result);
        assertEquals(capsules.size(), result.size());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetCapsulesUserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getCapsules(email));
    }

    @Disabled // todo hoanglon
    @Test
    void testRegisterUserSuccess() {
        String email = "test@example.com";
        String password = "securepassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        User user = userService.registerUser(password, email);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        String email = "test@example.com";
        String password = "securepassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(password, email));
    }

    @Test
    void testLoginUserSuccess() throws IllegalAccessException {
        String email = "test@example.com";
        String password = "securepassword";

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
        String email = "test@example.com";
        String password = "wrongpassword";

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
        String email = "test@example.com";
        String password = "securepassword";

        User user = new User();
        user.setRole(Role.ROLE_DELETED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserDeletedException.class, () -> userService.loginUser(email, password));
    }

    @Disabled // todo hoanglon@fel.cvut.cz FIX IT
    @Test
    void testDeleteUserSuccess() {
        String email = "test@example.com";

        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.deleteUser(email);

        assertEquals(Role.ROLE_DELETED, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(email));
    }


    @Test
    void testChangePasswordSuccess() {
        String email = "test@example.com";
        String currentPassword = "currentPassword";
        String newPassword = "newSecurePassword";

        User user = new User();
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        userService.changePassword(email, currentPassword, newPassword);

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePasswordIncorrectCurrentPassword() {
        String email = "test@example.com";
        String currentPassword = "wrongPassword";
        String newPassword = "newSecurePassword";

        User user = new User();
        user.setPassword("encodedCurrentPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, "encodedCurrentPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(email, currentPassword, newPassword));
    }
}