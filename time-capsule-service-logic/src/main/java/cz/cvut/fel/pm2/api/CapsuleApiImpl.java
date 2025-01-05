package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.config.security.JwtUtil;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.cvut.fel.pm2.service.CapsuleService.hashPassword;

/**
 * REST controller for capsule-related API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class CapsuleApiImpl implements CapsuleApi {
    private final CapsuleService capsuleService;
    private final CapsuleRepository capsuleRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Creates a new capsule.
     *
     * @param capsuleDto the capsule data transfer object
     * @param authHeader the authorization header containing the JWT token
     * @return the created capsule data transfer object
     * @throws NoSuchAlgorithmException if the hashing algorithm is not found
     */
    @Override
    public ResponseEntity<CapsuleDto> createCapsule(CapsuleDto capsuleDto, @RequestHeader("Authorization") String authHeader) throws NoSuchAlgorithmException {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.status(401).body(null);
            }
            if (userDetails.getUsername() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(capsuleService.createCapsule(capsuleDto, userDetails.getUsername()));
            }
            return ResponseEntity.status(401).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves capsules where the user is a contributor.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a list of capsules where the user is a contributor
     */
    public ResponseEntity<List<CapsuleDto>> getUserContributorCapsules(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
            List<CapsuleDto> capsules = capsuleService.findCapsulesWhereUserContributes(user.getId());
            return ResponseEntity.ok(capsules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves all capsules for the authenticated user.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a list of capsules for the authenticated user
     */
    @Override
    public ResponseEntity<List<CapsuleDto>> getCapsules(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.ok(capsuleService.getCapsules(email));
            }
            return ResponseEntity.ok(capsuleService.getCapsules(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sets the readiness state of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param ready the readiness state to set
     * @return the updated capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> readyCapsule(@RequestParam String capsuleId, @RequestParam boolean ready) {
        return ResponseEntity.ok(capsuleService.readyCapsule(capsuleId, ready));
    }

    /**
     * Opens a capsule via QR code.
     *
     * @param capsuleId the ID of the capsule
     * @param qrCodePasswordRaw the raw QR code password
     * @return a map containing a success message
     * @throws NoSuchAlgorithmException if the hashing algorithm is not found
     * @throws InvalidBodyException if the capsule ID or QR code password is invalid
     * @throws NotFoundException if the capsule is not found
     */
    @Override
    public ResponseEntity<Map<String, String>> openViaQr(@RequestParam String capsuleId, @RequestParam String qrCodePasswordRaw) throws NoSuchAlgorithmException {
        if (capsuleId == null || qrCodePasswordRaw == null || qrCodePasswordRaw.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        Long longCapsuleId = Long.parseLong(capsuleId);
        var capsule = capsuleRepository.getCapsuleById(longCapsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));
        String storedHashedPassword = capsule.getQrCodePassword();
        String hashedPasswordRaw = hashPassword(qrCodePasswordRaw);
        if (hashedPasswordRaw.equals(storedHashedPassword)) {
            capsuleService.tryUnlockCapsule(Long.parseLong(capsuleId));
            Map<String, String> response = new HashMap<>();
            capsuleService.updateUnlockMethodState(Long.parseLong(capsuleId), UnlockMethod.QR_CODE, true, true);
            response.put("message", "QR code password is correct, unlock method confirmed");
            return ResponseEntity.ok(response);
        } else {
            throw new InvalidBodyException("Wrong QR code password");
        }
    }

    /**
     * Subscribes a user to a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param userEmail the email of the user
     * @return the updated capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> subscribeToCapsule(String capsuleId, String userEmail) {
        return ResponseEntity.ok(capsuleService.subscribeToCapsule(capsuleId, userEmail));
    }

    /**
     * Retrieves the details of a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return the capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> getCapsuleDetails(String capsuleId) {
        return ResponseEntity.ok(capsuleService.getCapsuleDetails(capsuleId));
    }

    /**
     * Unlocks a capsule early.
     *
     * @param capsuleId the ID of the capsule
     * @return the updated capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> unlockCapsuleEarly(String capsuleId) {
        return ResponseEntity.ok(capsuleService.unlockCapsuleEarly(capsuleId));
    }

    /**
     * Locks a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return the updated capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> lockCapsule(String capsuleId) {
        return ResponseEntity.ok(capsuleService.lockCapsule(capsuleId));
    }

    /**
     * Unlocks a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return the updated capsule data transfer object
     */
    @Override
    public ResponseEntity<CapsuleDto> unlockCapsule(String capsuleId) {
        return ResponseEntity.ok(capsuleService.unlockCapsule(capsuleId));
    }
}
