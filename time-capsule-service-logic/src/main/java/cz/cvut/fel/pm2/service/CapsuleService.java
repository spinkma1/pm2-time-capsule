package cz.cvut.fel.pm2.service;
import cz.cvut.fel.pm2.UnlockMethodState;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepository;

    private final CapsuleMapper capsuleMapper;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final MailService mailService;

    private static final String NOT_FOUND_USER_MESSAGE = "User not found";
    private static final String NOT_FOUND_CAPSULE_MESSAGE = "Capsule not found";

    public CapsuleDto createCapsule(@NonNull CapsuleDto capsuleDto, @NonNull String email) throws NoSuchAlgorithmException {
        // Validace vstupního DTO
        validateCapsule(capsuleDto);

        Capsule capsule = capsuleMapper.toEntity(capsuleDto);


        try {

            User owner = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));
            capsule.setOwner(owner);
            capsule = capsuleRepository.save(capsule);
            List<User> users = new ArrayList<>();
            for (UserDto userDto : capsuleDto.users()) {
                User user = userRepository.findByEmail(userDto.email())
                        .orElseThrow(() -> {
                            mailService.sendEmail(
                                    owner.getEmail(),
                                    "Subscription Successful",
                                    "You have successfully subscribed to the capsule by : " + owner.getEmail() + "Please register to access the capsule. https://time-capsule-phi.vercel.app/"
                            );
                            return null;
                        });

                if (user != null) {
                    users.add(user);
                    if (!user.getCapsules().contains(capsule)) {
                        user.getCapsules().add(capsule);
                    }
                }
            }

            capsule.setUsers(users);

            capsule = capsuleRepository.save(capsule);

        } catch (DataIntegrityViolationException e) {
            throw new InvalidBodyException("Capsule with the same name already exists");
        } catch (Exception e) {
            // Zachycení ostatních chyb
            throw new RuntimeException("An error occurred while creating the capsule", e);
        }
        generateAndHashQrPassword(capsule.getId());


        return capsuleMapper.toDto(capsule);
    }


    public List<CapsuleDto> getCapsules(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));
        return capsuleRepository.getCapsulesByOwner(user)
                .map(capsuleMapper::toDtos)
                .orElseThrow(() -> new NotFoundException("No capsule was found for the specified user"));
    }

    @PreAuthorize("@adminUtils.checkForAdminRights()")
    public Boolean deleteCapsule(Long capsuleId) {
        Capsule capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE));
        User owner = capsule.getOwner();
        owner.getCapsules().remove(capsule);
        mailService.sendEmail(
                owner.getEmail(),
                "Capsule Deleted",
                "The capsule " + capsule.getName() + " has been deleted, because it didnt comply with the rules of our service. If you want to know more, please contact us."
        );
        capsuleRepository.delete(capsule);
        return true;
    }



    public void validateCapsule(CapsuleDto capsuleDto) {
        if (capsuleDto.name() == null ||
                capsuleDto.description() == null ||
                capsuleDto.capsuleSize() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
    }

    public CapsuleDto readyCapsule(String capsuleId, boolean ready) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        Optional<Capsule> capsule = capsuleRepository.getCapsuleByName(capsuleId);

        if (ready) {
            capsule.orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE)).setState(State.WAIT);
        }
        else {
            capsule.orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE)).setState(State.EDIT);
        }

        return capsuleMapper
                .toDto(
                        capsuleRepository.save(
                                capsule.orElseThrow(
                                        () -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE))));
    }



    public void generateAndHashQrPassword(Long capsuleId) throws NoSuchAlgorithmException {
        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE));

        String rawPassword;
        String hashedPassword;

        do {
            rawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            hashedPassword = hashPassword(rawPassword);

        } while (capsuleRepository.findByQrCodePassword(hashedPassword).isPresent());

        capsule.setQrCodePassword(hashedPassword);
        capsuleRepository.save(capsule);

    }

    public static String hashPassword(String rawPassword) throws NoSuchAlgorithmException {
        String hashedPassword;
        // hash with SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(rawPassword.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        hashedPassword = hexString.toString();
        return hashedPassword;
    }


    public void updateUnlockMethodState(Long capsuleId, UnlockMethod unlockMethod, boolean enabledBool, boolean completionBool) {
        // Retrieve the capsule by its ID
        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE));

        var unlockMethods = capsule.getUnlockMethods();

        if (unlockMethods.containsKey(unlockMethod)) {

            UnlockMethodState currentState = unlockMethods.get(unlockMethod);

            currentState.setEnabled(enabledBool);
            currentState.setComplete(completionBool);


            capsuleRepository.save(capsule);
            tryUnlockCapsule((long) capsuleId);
            capsuleRepository.save(capsule);

        } else {
            throw new InvalidBodyException("Unlock method does not exist");
        }
    }

    public void setCapsuleOpenLocation(String capsuleId, Double longitude, Double latitude) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE)).setUnlockLongit(longitude);
        capsule.orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE)).setUnlockLat(latitude);
        capsuleRepository.save(capsule.get());
    }

    /**
     * Sets how the capsule can be opened.
     * @param capsuleId the id of the capsule
     *
     */

    //either by time or by location or by scanning a qr code, or any combination of the three
    public void setCapsuleOpenMethod(String capsuleId, Set<UnlockMethod> methodSet) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        var unlockMethods = capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).getUnlockMethods();

        // Loop through the unlock methods and enable the ones that are in the methodSet
        for (Map.Entry<UnlockMethod, UnlockMethodState> entry : unlockMethods.entrySet()) {
            UnlockMethod method = entry.getKey();
            UnlockMethodState state = entry.getValue();

            // If the method is in the methodSet, enable it
            state.setEnabled(methodSet.contains(method));
        }

        capsuleRepository.save(capsule.get());
    }

    public void setCapsuleTime(String capsuleId, LocalDateTime time) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockTime(time);
        capsuleRepository.save(capsule.get());
    }



    public boolean tryUnlockCapsule(Long capsuleId) {
        // Fetch the capsule by its ID or throw an exception if not found
        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        var unlockMethods = capsule.getUnlockMethods();
        boolean allMethodsSatisfied = true;


        for (Map.Entry<UnlockMethod, UnlockMethodState> entry : unlockMethods.entrySet()) {
            UnlockMethod method = entry.getKey();
            UnlockMethodState state = entry.getValue();


            if (state.isEnabled() && !state.isComplete()) {
                allMethodsSatisfied = false;
                break;
            }
        }


        if (allMethodsSatisfied) {
            capsule.setState(State.OPEN);
            capsuleRepository.save(capsule);


            sendOpenNotifications(capsule);

            return true;
        }
        return false;
    }

    public CapsuleDto subscribeToCapsule(String capsuleId, String userEmail) {
        if (capsuleId == null || capsuleId.isEmpty() || userEmail == null || userEmail.isEmpty()) {
            throw new InvalidBodyException("Capsule ID or user email cannot be empty");
        }

        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (capsule.getUsers().contains(user)) {
            throw new InvalidBodyException("User is already subscribed to this capsule");
        }

        capsule.getUsers().add(user);
        capsuleRepository.save(capsule);

        mailService.sendEmail(
                user.getEmail(),
                "Subscription Successful",
                "You have been successfully subscribed to the capsule: " + capsule.getName()
        );

        return capsuleMapper.toDto(capsule);
    }



    // Scheduled task to notify users 1 day before the capsule becomes openable
    @Scheduled(cron = "0 0 8 * * *") // 8:00 Everyday
    public void notifyBeforeOpening() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        // Find capsules with unlock times within the next 24 hours
        var capsulesToNotify = capsuleRepository.findByUnlockTimeBetween(now, tomorrow);

        for (Capsule capsule : capsulesToNotify) {
            sendUpcomingOpenNotification(capsule);
        }
    }

    //Notify the owner and subscribed users about a new opened capsule through email
    private void sendOpenNotifications(Capsule capsule) {
        String subject = "Capsule Now Openable!";
        String message = String.format(
                "Hello,\n\nThe capsule '%s' is now openable! You can access it at your convenience.\n\nBest regards,\nMemory Capsule",
                capsule.getName()
        );

        // Notify owner
        String ownerEmail = capsule.getOwner().getEmail();
        if (ownerEmail != null) {
            mailService.sendEmail(ownerEmail, subject, message);
        } else {
            log.warn("Capsule owner email is null for capsule ID: {}", capsule.getId());
        }

        // Notify all subscribed users
        capsule.getUsers().forEach(user -> {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                mailService.sendEmail(userEmail, subject, message);
            } else {
                log.warn("Subscribed user email is null for capsule ID: {}", capsule.getId());
            }
        });
    }


    public List<CapsuleDto> findCapsulesByUser(Long userId) {
        List<CapsuleDto> capsules = capsuleRepository.findCapsulesByUser(userId).stream().map(capsuleMapper::toDto).toList();
        return capsules;
    }

    public List<CapsuleDto> findCapsulesWhereUserContributes(Long userId) {
        List<CapsuleDto> capsules = capsuleRepository.findCapsulesWhereUserContributes(userId).stream().map(capsuleMapper::toDto).toList();
        return capsules;
    }
    public List<CapsuleDto> findAllCapsulesForUser(Long userId) {
        // Fetch owned capsules
        List<CapsuleDto> ownedCapsules = findCapsulesByUser(userId);

        // Fetch contributed capsules
        List<CapsuleDto> contributedCapsules = findCapsulesWhereUserContributes(userId);

        // Combine both lists
        List<CapsuleDto> allCapsules = new ArrayList<>();
        allCapsules.addAll(ownedCapsules);
        allCapsules.addAll(contributedCapsules);

        return allCapsules;
    }

    //Notify the owner and subscribed users about an capsule, that will open soon, through email
    private void sendUpcomingOpenNotification(Capsule capsule) {
        String subject = "Capsule Will Be Openable Soon!";
        String message = String.format(
                "Hello,\n\nThe capsule '%s' will be openable tomorrow! Get ready to access its contents.\n\nBest regards,\nMemory Capsule",
                capsule.getName()
        );

        // Notify the owner
        String ownerEmail = capsule.getOwner().getEmail();
        if (ownerEmail != null) {
            mailService.sendEmail(ownerEmail, subject, message);
        } else {
            log.warn("Capsule owner email is null for capsule ID: {}", capsule.getId());
        }

        // Notify all subscribed users
        capsule.getUsers().forEach(user -> {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                mailService.sendEmail(userEmail, subject, message);
            } else {
                log.warn("Subscribed user email is null for capsule ID: {}", capsule.getId());
            }
        });
    }

    public CapsuleDto getCapsuleDetails(String capsuleId) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("Capsule ID is required");
        }

        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        return capsuleMapper.toDto(capsule);
    }

    public CapsuleDto unlockCapsuleEarly(String capsuleId) {
        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);
        capsuleRepository.save(capsule);

        capsule.getUsers().forEach(user -> {
            mailService.sendEmail(
                    user.getEmail(),
                    "Capsule Unlocked Early",
                    "The time capsule '" + capsule.getName() + "' has been unlocked early!"
            );
        });

        return capsuleMapper.toDto(capsule);
    }

    public CapsuleDto unlockCapsule(String capsuleId) {
        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);
        capsuleRepository.save(capsule);

        capsule.getUsers().forEach(user -> {
            mailService.sendEmail(
                    user.getEmail(),
                    "Capsule Unlocked",
                    "The time capsule '" + capsule.getName() + "' has been unlocked!"
            );
        });

        return capsuleMapper.toDto(capsule);
    }

    public CapsuleDto lockCapsule(String capsuleId) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("Capsule ID is required");
        }

        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        capsule.setState(State.WAIT);

        capsuleRepository.save(capsule);

        mailService.sendEmail(
                capsule.getOwner().getEmail(),
                "Capsule Locked",
                "Your capsule '" + capsule.getName() + "' has been locked and is ready to be opened based on its unlock methods."
        );

        return capsuleMapper.toDto(capsule);
    }

}
