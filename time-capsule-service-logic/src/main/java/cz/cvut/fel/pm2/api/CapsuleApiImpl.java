package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static cz.cvut.fel.pm2.service.CapsuleService.hashPassword;

@RestController
@RequiredArgsConstructor
public class CapsuleApiImpl implements CapsuleApi {
    private final CapsuleService capsuleService;
    private final CapsuleRepository capsuleRepository;

    @Override
    public void createCapsule(CapsuleDto capsule) throws NoSuchAlgorithmException {

        if (capsule.name() == null || capsule.description() == null ||
                capsule.teamWork() == null ||
                capsule.userFileLimit() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
        capsuleService.createCapsule(capsule);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Capsule created successfully");
    }

    public CapsuleDto getCapsule(@RequestParam String email) {
        return capsuleService.getCapsule(email);
    }
    @Override
    public void readyCapsule(@RequestParam String capsuleId, @RequestParam boolean ready) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        capsuleService.readyCapsule(capsuleId, ready);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Capsule status updated successfully");
    }

    @Override
    public ResponseEntity<Map<String, String>> openViaQr(@RequestParam String capsuleId, @RequestParam String qrCodePasswordRaw) throws NoSuchAlgorithmException {
        // Check if the necessary parameters are provided
        if (capsuleId == null || qrCodePasswordRaw == null || qrCodePasswordRaw.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        int intCapsuleId = Integer.parseInt(capsuleId);

        // Get the capsule from the repository
        var capsule = capsuleRepository.getCapsuleById(intCapsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));

        // Get the stored hashed password
        String storedHashedPassword = capsule.getQrCodePassword();

        // Hash the provided QR code password and compare with the stored hash
        String hashedPasswordRaw = hashPassword(qrCodePasswordRaw);
        if (hashedPasswordRaw.equals(storedHashedPassword)) {
            capsuleService.tryUnlockCapsule(Integer.parseInt(capsuleId));

            // Send a successful response
            Map<String, String> response = new HashMap<>();
            capsuleService.updateUnlockMethodState(Integer.parseInt(capsuleId), UnlockMethod.QR_CODE, true,true);
            response.put("message", "QR code password is correct, unlock method confirmed");
            return ResponseEntity.ok(response);

        } else {
            // If the passwords don't match
            throw new InvalidBodyException("Wrong QR code password");
        }
    }


}
