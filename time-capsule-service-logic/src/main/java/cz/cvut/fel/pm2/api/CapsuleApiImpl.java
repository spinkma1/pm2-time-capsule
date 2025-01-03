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

@RestController
@RequiredArgsConstructor
public class CapsuleApiImpl implements CapsuleApi {
    private final CapsuleService capsuleService;
    private final CapsuleRepository capsuleRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;


    @Override
    public ResponseEntity<CapsuleDto> createCapsule(CapsuleDto capsuleDto, @RequestHeader("Authorization") String authHeader) throws NoSuchAlgorithmException {
        try {
            String token = authHeader.substring(7);

            String email = jwtUtil.extractUsername(token);

            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.status(401).body(null);

            }
            if (userDetails.getUsername() !=null) {

                return ResponseEntity.status(HttpStatus.CREATED).body(capsuleService.createCapsule(capsuleDto, userDetails.getUsername()));
            }
            return ResponseEntity.status(401).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

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

    @Override
    public ResponseEntity<CapsuleDto> readyCapsule(@RequestParam String capsuleId, @RequestParam boolean ready) {
        return ResponseEntity.ok(capsuleService.readyCapsule(capsuleId, ready));
    }

    @Override
    public ResponseEntity<Map<String, String>> openViaQr(@RequestParam String capsuleId, @RequestParam String qrCodePasswordRaw) throws NoSuchAlgorithmException {
        if (capsuleId == null || qrCodePasswordRaw == null || qrCodePasswordRaw.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

         Long longCapsuleId = Long.parseLong(capsuleId);

        // Get the capsule from the repository
        var capsule = capsuleRepository.getCapsuleById(longCapsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        // Get the stored hashed password
        String storedHashedPassword = capsule.getQrCodePassword();

        // Hash the provided QR code password and compare with the stored hash
        String hashedPasswordRaw = hashPassword(qrCodePasswordRaw);
        if (hashedPasswordRaw.equals(storedHashedPassword)) {
            capsuleService.tryUnlockCapsule(Long.parseLong(capsuleId));

            // Send a successful response
            Map<String, String> response = new HashMap<>();
            capsuleService.updateUnlockMethodState(Long.parseLong(capsuleId), UnlockMethod.QR_CODE, true,true);
            response.put("message", "QR code password is correct, unlock method confirmed");
            return ResponseEntity.ok(response);

        } else {
            // If the passwords don't match
            throw new InvalidBodyException("Wrong QR code password");
        }
    }

    @Override
    public ResponseEntity<CapsuleDto> subscribeToCapsule(@RequestParam String capsuleId, @RequestParam String userEmail) {
        return ResponseEntity.ok(capsuleService.subscribeToCapsule(capsuleId, userEmail));
    }

    @Override
    public ResponseEntity<CapsuleDto> getCapsuleDetails(@RequestParam String capsuleId) {
        return ResponseEntity.ok(capsuleService.getCapsuleDetails(capsuleId));
    }

    @Override
    public ResponseEntity<CapsuleDto> unlockCapsuleEarly(@RequestParam String capsuleId) {
        return ResponseEntity.ok(capsuleService.unlockCapsuleEarly(capsuleId));
    }


}
