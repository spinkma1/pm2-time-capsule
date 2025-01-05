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

    private final MailService mailService;

    private static final String NOT_FOUND_USER_MESSAGE = "User not found";
    private static final String NOT_FOUND_CAPSULE_MESSAGE = "Capsule not found";

    /**
     * Creates a new capsule.
     *
     * @param capsuleDto the capsule data transfer object
     * @param email the email of the user creating the capsule
     * @return the created capsule data transfer object
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available
     */
    public CapsuleDto createCapsule(@NonNull CapsuleDto capsuleDto, @NonNull String email) throws NoSuchAlgorithmException {
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
                        .orElse(null);
                if (user == null) {
                    mailService.sendEmail(
                            userDto.email(),
                            "Subscription Successful",
                            "You have successfully subscribed to the capsule by : " + owner.getEmail() + ". Please register to access the capsule. https://time-capsule-phi.vercel.app/"
                    );
                }
                else{
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
            throw new RuntimeException("An error occurred while creating the capsule", e);
        }
        generateAndHashQrPassword(capsule.getId());


        return capsuleMapper.toDto(capsule);
    }

    /**
     * Retrieves the capsules owned by a user.
     *
     * @param email the email of the user
     * @return the list of capsule data transfer objects
     */
    public List<CapsuleDto> getCapsules(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));
        return capsuleRepository.getCapsulesByOwner(user)
                .map(capsuleMapper::toDtos)
                .orElseThrow(() -> new NotFoundException("No capsule was found for the specified user"));
    }
    /**
     * Deletes a capsule.
     *
     * @param capsuleId the ID of the capsule to delete
     * @return true if the capsule was deleted successfully
     */
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


    /**
     * Validates the capsule data transfer object.
     *
     * @param capsuleDto the capsule data transfer object
     */
    public void validateCapsule(CapsuleDto capsuleDto) {
        if (capsuleDto.name() == null ||
                capsuleDto.description() == null ||
                capsuleDto.capsuleSize() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
    }
    /**
     * Sets the state of a capsule to ready or edit.
     *
     * @param capsuleId the ID of the capsule
     * @param ready the state to set
     * @return the updated capsule data transfer object
     */
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


    /**
     * Generates and hashes a QR code password for a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available
     */
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
    /**
     * Hashes a password using SHA-256.
     *
     * @param rawPassword the raw password
     * @return the hashed password
     * @throws NoSuchAlgorithmException if the hashing algorithm is not available
     */
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

    /**
     * Updates the unlock method state of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param unlockMethod the unlock method
     * @param enabledBool the enabled state
     * @param completionBool the completion state
     */
    public void updateUnlockMethodState(Long capsuleId, UnlockMethod unlockMethod, boolean enabledBool, boolean completionBool) {
        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_CAPSULE_MESSAGE));

        var unlockMethods = capsule.getUnlockMethods();

        if (unlockMethods.containsKey(unlockMethod)) {

            UnlockMethodState currentState = unlockMethods.get(unlockMethod);

            currentState.setEnabled(enabledBool);
            currentState.setComplete(completionBool);


            capsuleRepository.save(capsule);
            tryUnlockCapsule(capsuleId);
            capsuleRepository.save(capsule);

        } else {
            throw new InvalidBodyException("Unlock method does not exist");
        }
    }
    /**
     * Sets the open location of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param longitude the longitude
     * @param latitude the latitude
     */
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

    public void setCapsuleOpenMethod(String capsuleId, Set<UnlockMethod> methodSet) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        var unlockMethods = capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).getUnlockMethods();

        for (Map.Entry<UnlockMethod, UnlockMethodState> entry : unlockMethods.entrySet()) {
            UnlockMethod method = entry.getKey();
            UnlockMethodState state = entry.getValue();

            state.setEnabled(methodSet.contains(method));
        }

        capsuleRepository.save(capsule.get());
    }
    /**
     * Sets to unlock time of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param time to unlock time
     */
    public void setCapsuleTime(String capsuleId, LocalDateTime time) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockTime(time);
        capsuleRepository.save(capsule.get());
    }


    /**
     * Tries to unlock a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return true if the capsule was unlocked successfully
     */
    public boolean tryUnlockCapsule(Long capsuleId) {

        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        var unlockMethods = capsule.getUnlockMethods();
        boolean allMethodsSatisfied = true;


        for (Map.Entry<UnlockMethod, UnlockMethodState> entry : unlockMethods.entrySet()) {
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
    /**
     * Subscribes a user to a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param userEmail the email of the user
     * @return the updated capsule data transfer object
     */
    public CapsuleDto subscribeToCapsule(String capsuleId, String userEmail) {
        if (capsuleId == null || capsuleId.isEmpty() || userEmail == null || userEmail.isEmpty()) {
            throw new InvalidBodyException("Capsule ID or user email cannot be empty");
        }


        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));


        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if (userOpt.isEmpty()) {

            mailService.sendEmail(
                    userEmail,
                    "Subscription Invitation",
                    "You have been invited to join a capsule. Please register to access the capsule at: https://time-capsule-phi.vercel.app/"
            );
            return capsuleMapper.toDto(capsule);
        }

        User user = userOpt.get();


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



    /**
     * Notifies users 1 day before the capsule becomes openable.
     */
    @Scheduled(cron = "0 0 8 * * *") // 8:00 Everyday
    public void notifyBeforeOpening() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        var capsulesToNotify = capsuleRepository.findByUnlockTimeBetween(now, tomorrow);

        for (Capsule capsule : capsulesToNotify) {
            sendUpcomingOpenNotification(capsule);
        }
    }

    /**
     * Sends notifications to the owner and subscribed users about a newly opened capsule.
     *
     * @param capsule the capsule
     */
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

    /**
     * Finds capsules owned by a user.
     *
     * @param userId the ID of the user
     * @return the list of capsule data transfer objects
     */
    public List<CapsuleDto> findCapsulesByUser(Long userId) {
        return capsuleRepository.findCapsulesByUser(userId).stream().map(capsuleMapper::toDto).toList();
    }
    /**
     * Finds capsules where a user contributes.
     *
     * @param userId the ID of the user
     * @return the list of capsule data transfer objects
     */
    public List<CapsuleDto> findCapsulesWhereUserContributes(Long userId) {
        return capsuleRepository.findCapsulesWhereUserContributes(userId).stream().map(capsuleMapper::toDto).toList();
    }
    /**
     * Finds all capsules for a user.
     *
     * @param userId the ID of the user
     * @return the list of capsule data transfer objects
     */
    public List<CapsuleDto> findAllCapsulesForUser(Long userId) {
        List<CapsuleDto> ownedCapsules = findCapsulesByUser(userId);

        List<CapsuleDto> contributedCapsules = findCapsulesWhereUserContributes(userId);


        List<CapsuleDto> allCapsules = new ArrayList<>();
        allCapsules.addAll(ownedCapsules);
        allCapsules.addAll(contributedCapsules);

        return allCapsules;
    }
    /**
     * Sends notifications to the owner and subscribed users about a capsule that will open soon.
     *
     * @param capsule the capsule
     */

    private void sendUpcomingOpenNotification(Capsule capsule) {
        String subject = "Capsule Will Be Openable Soon!";
        String message = String.format(
                "Hello,\n\nThe capsule '%s' will be openable tomorrow! Get ready to access its contents.\n\nBest regards,\nMemory Capsule",
                capsule.getName()
        );

        String ownerEmail = capsule.getOwner().getEmail();
        if (ownerEmail != null) {
            mailService.sendEmail(ownerEmail, subject, message);
        } else {
            log.warn("Capsule owner email is null for capsule ID: {}", capsule.getId());
        }

        capsule.getUsers().forEach(user -> {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                mailService.sendEmail(userEmail, subject, message);
            } else {
                log.warn("Subscribed user email is null for capsule ID: {}", capsule.getId());
            }
        });
    }
    /**
     * Retrieves the details of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return the capsule data transfer object
     */
    public CapsuleDto getCapsuleDetails(String capsuleId) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("Capsule ID is required");
        }

        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        return capsuleMapper.toDto(capsule);
    }
    /**
     * Unlocks a capsule early.
     *
     * @param capsuleId the ID of the capsule
     * @return the updated capsule data transfer object
     */
    public CapsuleDto unlockCapsuleEarly(String capsuleId) {
        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);
        capsuleRepository.save(capsule);

        capsule.getUsers().forEach(user -> mailService.sendEmail(
                user.getEmail(),
                "Capsule Unlocked Early",
                "The time capsule '" + capsule.getName() + "' has been unlocked early!"
        ));

        return capsuleMapper.toDto(capsule);
    }
    /**
     * Unlocks a capsule.
     *
     * @param capsuleId the ID of the capsule to unlock
     * @return the updated capsule data transfer object
     * @throws NotFoundException if the capsule is not found
     */
    public CapsuleDto unlockCapsule(String capsuleId) {
        Capsule capsule = capsuleRepository.getCapsuleById(Long.parseLong(capsuleId))
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        capsule.setUnlockTime(LocalDateTime.now());
        capsule.setState(State.OPEN);
        capsuleRepository.save(capsule);

        capsule.getUsers().forEach(user -> mailService.sendEmail(
                user.getEmail(),
                "Capsule Unlocked",
                "The time capsule '" + capsule.getName() + "' has been unlocked!"
        ));

        return capsuleMapper.toDto(capsule);
    }
    /**
     * Locks a capsule.
     *
     * @param capsuleId the ID of the capsule to lock
     * @return the updated capsule data transfer object
     * @throws InvalidBodyException if the capsule ID is null or empty
     * @throws NotFoundException if the capsule is not found
     */
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
