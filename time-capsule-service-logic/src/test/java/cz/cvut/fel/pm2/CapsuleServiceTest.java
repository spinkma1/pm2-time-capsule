
package cz.cvut.fel.pm2;
/*
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UnlockMethodsDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
class CapsuleServiceTest {

    @InjectMocks
    private CapsuleService capsuleService;

    @Mock
    private CapsuleRepository capsuleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CapsuleMapper capsuleMapper;

    @Mock
    private MailService mailService;

    private CapsuleDto capsuleDto = new CapsuleDto(
            1L,
            "Test Capsule",
            "This is a test capsule description",
            true,
            1024L,
            LocalDateTime.now().plusDays(30),
            "testPassword123",
            50.0755,
            14.4378,
            List.of(
                    new UserDto(1L, "User One", "User One", "Nothing", "Owner", List.of(), List.of()),
                    new UserDto(2L, "User Two", "User 2", "Nothing", "Owner", List.of(), List.of())
            ),
            new UnlockMethodsDto(true, false, false, false, false, false, false, false),
            "WAIT"
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubscribeToCapsuleSuccess() {

        String capsuleId = "testCapsule";
        String userEmail = "user@example.com";

        Capsule capsule = new Capsule();
        capsule.setName(capsuleId);
        capsule.setUsers(new ArrayList<>());


        User user = new User();
        user.setEmail(userEmail);


        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.of(capsule));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(capsuleMapper.toDto(capsule)).thenReturn(capsuleDto);

        doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString());


        CapsuleDto result = capsuleService.subscribeToCapsule(capsuleId, userEmail);


        assertNotNull(result);
        assertEquals(capsuleDto.name(), result.name());


        assertTrue(capsule.getUsers().contains(user));
        verify(capsuleRepository).save(capsule);

        verify(mailService).sendEmail(eq(userEmail), contains("Subscription Successful"), contains("You have been successfully subscribed to the capsule"));
    }

    @Test
    void testSubscribeToCapsuleNotFound() {

        String capsuleId = "nonexistent";
        String userEmail = "user@example.com";


        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> capsuleService.subscribeToCapsule(capsuleId, userEmail));
    }

    @Test
    void testGetCapsulesSuccess() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        Capsule capsule = new Capsule();
        capsule.setName("Capsule1");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(capsuleRepository.getCapsulesByOwner(user)).thenReturn(Optional.of(List.of(capsule)));
        when(capsuleMapper.toDtos(List.of(capsule))).thenReturn(List.of(capsuleDto));

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
        String capsuleId = "CapsuleID";
        Capsule capsule = new Capsule();
        capsule.setUnlockTime(LocalDateTime.now().plusDays(1));


        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.of(capsule));
        when(capsuleRepository.save(capsule)).thenReturn(capsule);
        when(capsuleMapper.toDto(capsule)).thenReturn(capsuleDto);

        CapsuleDto result = capsuleService.unlockCapsuleEarly(capsuleId);

        assertNotNull(result);
        assertEquals(LocalDateTime.now().toLocalDate(), capsule.getUnlockTime().toLocalDate());
        verify(mailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSetCapsuleTimeSuccess() {
        String capsuleId = "CapsuleID";
        LocalDateTime newTime = LocalDateTime.now().plusDays(5);
        Capsule capsule = new Capsule();

        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.of(capsule));
        when(capsuleRepository.save(capsule)).thenReturn(capsule);

        capsuleService.setCapsuleTime(capsuleId, newTime);

        assertEquals(newTime, capsule.getUnlockTime());
        verify(capsuleRepository).save(capsule);
    }

    @Test
    void testGetCapsuleDetailsSuccess() {
        String capsuleId = "testCapsule";

        Capsule capsule = new Capsule();
        capsule.setName(capsuleId);


        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.of(capsule));
        when(capsuleMapper.toDto(capsule)).thenReturn(capsuleDto);

        CapsuleDto result = capsuleService.getCapsuleDetails(capsuleId);

        assertNotNull(result);
        assertEquals(capsuleDto.name(), result.name());
        assertEquals(capsuleDto.description(), result.description());

        verify(capsuleRepository).getCapsuleByName(capsuleId);
        verify(capsuleMapper).toDto(capsule);
    }

    @Test
    void testGetCapsuleDetailsNotFound() {
        String capsuleId = "nonexistent";

        when(capsuleRepository.getCapsuleByName(capsuleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> capsuleService.getCapsuleDetails(capsuleId));
    }

    @Test
    void testGetCapsuleDetailsInvalidId() {
        assertThrows(InvalidBodyException.class, () -> capsuleService.getCapsuleDetails(null));
        assertThrows(InvalidBodyException.class, () -> capsuleService.getCapsuleDetails(""));
    }
}
*/