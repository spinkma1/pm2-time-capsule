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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepository;

    private final CapsuleMapper capsuleMapper;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public CapsuleDto createCapsule(@NonNull CapsuleDto capsuleDto) throws NoSuchAlgorithmException {
        validateCapsule(capsuleDto);
        Capsule capsule = capsuleMapper.toEntity(capsuleDto);

        generateAndHashQrPassword(capsule.getId());
        capsule = capsuleRepository.save(capsule);

        return capsuleMapper.toDto(capsule);
    }

    public CapsuleDto getCapsule(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return capsuleRepository.getCapsulesByOwner(user)
                .map(capsuleMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));
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



    public void generateAndHashQrPassword(Integer capsuleId) throws NoSuchAlgorithmException {
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


    public void updateUnlockMethodState(int capsuleId, UnlockMethod unlockMethod, boolean enabledBool, boolean completionBool) {
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
            if (methodSet.contains(method)) {
                state.setEnabled(true);
            } else {
                state.setEnabled(false);
            }
        }

        capsuleRepository.save(capsule.get());
    }

    public void setCapsuleTime(String capsuleId, LocalDateTime time) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockTime(time);
        capsuleRepository.save(capsule.get());
    }



    public boolean tryUnlockCapsule(int capsuleId) {
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
            return true;
        }
        return false;
    }

}
