package cz.cvut.fel.pm2.service;
import cz.cvut.fel.pm2.UnlockMethodState;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public CapsuleDto createCapsule(@NonNull CapsuleDto capsuleDto) throws NoSuchAlgorithmException {
        validateCapsule(capsuleDto);

        Capsule capsule = capsuleMapper.toEntity(capsuleDto);

        capsule = capsuleRepository.save(capsule);
//
//        generateAndHashQrPassword(capsule.getId());
//        capsule = capsuleRepository.save(capsule);

//todo
        try {
            User user = userRepository.findById(capsuleDto.userId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            capsule.setOwner(user);
            capsule = capsuleRepository.save(capsule);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidBodyException("Capsule with the same name already exists");
        }
        generateAndHashQrPassword(capsule.getId());


        return capsuleMapper.toDto(capsule);
    }
 
    public List<CapsuleDto> getCapsules(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return capsuleRepository.getCapsulesByOwner(user)
                .map(capsuleMapper::toDtos)
                .orElseThrow(() -> new NotFoundException("No capsule was found for the specified user"));
    }


    public void validateCapsule(CapsuleDto capsuleDto) {
        if (capsuleDto.name() == null ||
                capsuleDto.description() == null ||
                capsuleDto.teamWork() == null ||
                capsuleDto.userFileLimit() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
    }

    public CapsuleDto readyCapsule(String capsuleId, boolean ready) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        Optional<Capsule> capsule = capsuleRepository.getCapsuleByName(capsuleId);

        if (ready) {
            capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setState(State.WAIT);
        }
        else {
            capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setState(State.EDIT);
        }

        return capsuleMapper.toDto(capsuleRepository.save(capsule.get()));
    }



    public void generateAndHashQrPassword(Long capsuleId) throws NoSuchAlgorithmException {
        var capsule = capsuleRepository.getCapsuleById(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

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
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        // Retrieve the unlockMethods map for this capsule
        var unlockMethods = capsule.getUnlockMethods();

        // Check if the unlock method exists in the map
        if (unlockMethods.containsKey(unlockMethod)) {
            // Get the current state of the unlock method
            UnlockMethodState currentState = unlockMethods.get(unlockMethod);

            // Update the state: set the new enabled and complete values
            currentState.setEnabled(enabledBool);
            currentState.setComplete(completionBool);

            // Save the updated capsule back to the repository
            capsuleRepository.save(capsule);
            tryUnlockCapsule(capsuleId);
            capsuleRepository.save(capsule);

        } else {
            throw new InvalidBodyException("Unlock method does not exist");
        }
    }

    public void setCapsuleOpenLocation(String capsuleId, Double longitude, Double latitude) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockLongit(longitude);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockLat(latitude);
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

        // Loop through the unlock methods and check if each enabled method is complete
        for (Map.Entry<UnlockMethod, UnlockMethodState> entry : unlockMethods.entrySet()) {
            UnlockMethod method = entry.getKey();
            UnlockMethodState state = entry.getValue();

            // If the method is enabled, we check if it's complete
            if (state.isEnabled() && !state.isComplete()) {
                allMethodsSatisfied = false;
                break; // Exit the loop early as we found an unsatisfied method
            }
        }

        // If all methods are satisfied, set the capsule to OPEN
        if (allMethodsSatisfied) {
            capsule.setState(State.OPEN);
            capsuleRepository.save(capsule); // Don't forget to persist the change

            // Send email notification
            sendOpenNotifications(capsule);

            return true;
        }
        return false;
    }

    public void subscribeToCapsule(String capsuleId, String userEmail) {
        Capsule capsule = capsuleRepository.getCapsuleByName(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (capsule.getUsers().contains(user)) {
            throw new InvalidBodyException("User is already subscribed to this capsule");
        }

        capsule.getUsers().add(user);
        capsuleRepository.save(capsule);

        // Notify the user, that he was subscribed to a capsule, through email
        mailService.sendEmail(
                user.getEmail(),
                "Subscription Successful",
                "You have successfully subscribed to the capsule: " + capsule.getName()
        );
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


}
