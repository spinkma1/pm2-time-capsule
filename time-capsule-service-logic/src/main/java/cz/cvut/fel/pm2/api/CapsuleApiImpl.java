package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Override
    public ResponseEntity<CapsuleDto> createCapsule(CapsuleDto capsuleDto) throws NoSuchAlgorithmException {
        return ResponseEntity.status(HttpStatus.CREATED).body(capsuleService.createCapsule(capsuleDto));
    }

    @Override
    public ResponseEntity<List<CapsuleDto>> getCapsules(@RequestParam String email) {
        return ResponseEntity.ok(capsuleService.getCapsules(email));
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

    @PostMapping("/capsules/{capsuleId}/subscribe")
    public ResponseEntity<String> subscribeToCapsule(
            //TODO
            @PathVariable String capsuleId,
            @RequestParam String userEmail) {
        capsuleService.subscribeToCapsule(capsuleId, userEmail);
        return ResponseEntity.ok(userEmail + "successfully subscribed to the capsule: " + capsuleId);
    }


}
