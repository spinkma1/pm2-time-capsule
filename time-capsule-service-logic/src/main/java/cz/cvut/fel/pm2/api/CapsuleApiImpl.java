package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CapsuleApiImpl implements CapsuleApi {
    private final CapsuleService capsuleService;

    @PostMapping("/capsule/create")
    @Override
    public void createCapsule(CapsuleDto capsule) {

        if (capsule.name() == null || capsule.description() == null ||
                capsule.teamWork() == null ||
                capsule.userFileLimit() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
        capsuleService.createCapsule(capsule);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Capsule created successfully");
    }

    @GetMapping("/capsule")
    public CapsuleDto getCapsule(@RequestParam String email) {
        return capsuleService.getCapsule(email);
    }
    @PostMapping("/capsule/ready")
    @Override
    public void readyCapsule(@RequestParam String capsuleId, @RequestParam boolean ready) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new InvalidBodyException("No or wrong body was sent");
        }

        capsuleService.readyCapsule(capsuleId, ready);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Capsule status updated successfully");
    }


}
