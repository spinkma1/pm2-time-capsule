package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.enums.Role;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapperImpl;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UnlockMethodsDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CapsuleServiceSBTest {

    @Autowired
    private CapsuleService capsuleService;

    @MockBean
    private CapsuleRepository capsuleRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private CapsuleMapperImpl capsuleMapper;

    @Autowired
    private MailService mailService;

    @Test
    void testSubscribeToCapsuleSuccess() {
        Long capsuleId = 1L;
        String capsuleName = "Test Capsule";
        String userEmail = "user@example.com";

        Capsule capsule = new Capsule();
        capsule.setId(capsuleId);
        capsule.setName(capsuleName);
        capsule.setUsers(new ArrayList<>());
        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);

        User user = new User();
        user.setEmail(userEmail);

        CapsuleDto capsuleDto = new CapsuleDto(
                capsuleId, new UserDto(1L, "User One", "User One", "Nothing", "Owner", List.of(), List.of()),
                capsuleName, "Test description", true, 1024L, LocalDateTime.now().toString(), "password",
                50.0755, 14.4378, List.of(), new UnlockMethodsDto(true, false, false, false, false, false, false, false),
                "WAIT", List.of()
        );

        when(capsuleRepository.getCapsuleById(capsuleId)).thenReturn(Optional.of(capsule));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
//        when(capsuleMapper.toDto(capsule)).thenReturn(capsuleDto);

//        doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString());

        CapsuleDto result = capsuleService.subscribeToCapsule(String.valueOf(capsuleId), userEmail);

        assertNotNull(result);
        assertEquals(capsuleDto.name(), result.name());
        assertTrue(capsule.getUsers().contains(user));

        verify(capsuleRepository).save(capsule);
    }

    @Test
    void testSubscribeToCapsuleNotFound() {
        String capsuleId = "9999";
        String userEmail = "user@example.com";

        when(capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> capsuleService.subscribeToCapsule(capsuleId, userEmail));
    }

    @Test
    void testSubscribeToCapsuleNotID() {
        String capsuleId = "notNumber";
        String userEmail = "user@example.com";

        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.empty());

        assertThrows(NumberFormatException.class, () -> capsuleService.subscribeToCapsule(capsuleId, userEmail));
    }
    @Test
    void testGetCapsulesSuccess() {
        String email = "user@example.com";
        Long capsuleId = 1L;

        User user = new User();
        user.setEmail(email);

        Capsule capsule = new Capsule();
        capsule.setName("Capsule1");
        capsule.setId(capsuleId);
        capsule.setUsers(new ArrayList<>());
        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);

        CapsuleDto capsuleDto = new CapsuleDto(1L, new UserDto(1L, "User One", "User One", "Nothing", "Owner", List.of(), List.of()),
                "Capsule1", "Description", true, 1024L, LocalDateTime.now().toString(), "password",
                50.0755, 14.4378, List.of(), new UnlockMethodsDto(true, false, false, false, false, false, false, false),
                "WAIT", List.of());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(capsuleRepository.getCapsulesByOwner(user)).thenReturn(Optional.of(List.of(capsule)));
//        when(capsuleMapper.toDtos(List.of(capsule))).thenReturn(List.of(capsuleDto));

        List<CapsuleDto> result = capsuleService.getCapsules(email);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).findByEmail(email);
        verify(capsuleRepository).getCapsulesByOwner(user);
    }

    @Test
    void testGetCapsulesNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> capsuleService.getCapsules(email));
    }

    @Test
    void testUnlockCapsuleEarlySuccess() {
        Long capsuleId = 1L;
        String capsuleName = "Capsule";
        Capsule capsule = new Capsule();
        capsule.setName(capsuleName);
        capsule.setId(capsuleId);
        capsule.setUsers(new ArrayList<>());
        capsule.setState(State.OPEN);
        capsule.setUnlockTime(LocalDateTime.now().plusDays(1));

        CapsuleDto capsuleDto = new CapsuleDto(1L, null, "CapsuleID", null, false, 0L, null,
                null, 0.0, 0.0, null, null, null, null);

        when(capsuleRepository.getCapsuleById(capsuleId)).thenReturn(Optional.of(capsule));
        when(capsuleRepository.save(capsule)).thenReturn(capsule);
//        when(capsuleMapper.toDto(capsule)).thenReturn(capsuleDto);

        CapsuleDto result = capsuleService.unlockCapsuleEarly(String.valueOf(capsuleId));

        assertNotNull(result);
        assertEquals(LocalDateTime.now().toLocalDate(), capsule.getUnlockTime().toLocalDate());
    }

    @Test
    void testCreateCapsuleInvalidBody() {
        String ownerEmail = "owner@example.com";

        // CapsuleDto with missing name
        CapsuleDto invalidCapsuleDto = new CapsuleDto(
                null,
                new UserDto(1L, "Owner One", "Owner One", "Nothing", "Owner", List.of(), List.of()),
                null, // Missing name
                "Description",
                true,
                1024L,
                LocalDateTime.now().plusDays(30).toString(),
                "password123",
                50.0755,
                14.4378,
                List.of(),
                new UnlockMethodsDto(true, false, false, false, false, false, false, false),
                "WAIT",
                List.of()
        );

        assertThrows(InvalidBodyException.class, () -> capsuleService.createCapsule(invalidCapsuleDto, ownerEmail));
    }


    @Test
    void testUpdateUnlockMethodStateSuccess() {
        Long capsuleId = 1L;
        UnlockMethod unlockMethod = UnlockMethod.QR_CODE;
        boolean enabledBool = true;
        boolean completionBool = true;

        User newUser = new User();

        newUser.setEmail("user@example.com");
        newUser.setPassword("securePassword");
        newUser.setRole(Role.ROLE_REGISTERED);

        newUser.setGoogleId("12345-google-id");
        newUser.setBio("This is a sample bio for the user.");
        newUser.setName("John Doe");


        newUser.setFollowers(new ArrayList<>());
        newUser.setFollowing(new ArrayList<>());
        newUser.setCapsules(new ArrayList<>());
        newUser.setNotifications(new ArrayList<>());
        Capsule capsule = new Capsule();
        capsule.setId(capsuleId);
        capsule.setName("Test Capsule");
        capsule.setUnlockMethods(new HashMap<>());
        capsule.getUnlockMethods().put(unlockMethod, new UnlockMethodState(false, false));
        capsule.setOwner(newUser);

        CapsuleDto capsuleDto = new CapsuleDto(
                capsuleId, new UserDto(1L, "User One", "User One", "Nothing", "Owner", List.of(), List.of()),
                "capsuleName", "Test description", true, 1024L, LocalDateTime.now().toString(), "password",
                50.0755, 14.4378, List.of(), new UnlockMethodsDto(true, false, false, false, false, false, false, false),
                "WAIT", List.of()
        );

        when(capsuleRepository.getCapsuleById(capsuleId)).thenReturn(Optional.of(capsule));
        when(capsuleRepository.save(capsule)).thenReturn(capsule);

        capsuleService.updateUnlockMethodState(capsuleId, unlockMethod, enabledBool, completionBool);

        UnlockMethodState updatedState = capsule.getUnlockMethods().get(unlockMethod);
        assertTrue(updatedState.isEnabled());
        assertTrue(updatedState.isComplete());

    }

    @Test
    void testUpdateUnlockMethodStateMethodDoesNotExist() {
        Long capsuleId = 1L;
        UnlockMethod unlockMethod = null; // Assume this method does not exist
        boolean enabledBool = true;
        boolean completionBool = true;

        Capsule capsule = new Capsule();
        capsule.setId(capsuleId);
        capsule.setName("Test Capsule");
        capsule.setUnlockMethods(new HashMap<>()); // Empty map

        when(capsuleRepository.getCapsuleById(capsuleId)).thenReturn(Optional.of(capsule));

        assertThrows(InvalidBodyException.class, () ->
                capsuleService.updateUnlockMethodState(capsuleId, unlockMethod, enabledBool, completionBool)
        );
    }

    @Test
    void testUpdateUnlockMethodStateCapsuleNotFound() {
        Long capsuleId = 999L;
        UnlockMethod unlockMethod = UnlockMethod.QR_CODE;
        boolean enabledBool = true;
        boolean completionBool = true;

        when(capsuleRepository.getCapsuleById(capsuleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                capsuleService.updateUnlockMethodState(capsuleId, unlockMethod, enabledBool, completionBool)
        );
    }
}