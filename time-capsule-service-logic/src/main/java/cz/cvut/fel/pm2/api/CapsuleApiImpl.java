package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CapsuleApiImpl implements CapsuleApi {
    private final CapsuleService capsuleService;

    @Override
    public void createCapsule(CapsuleDto capsule) {

        if (capsule.name() == null || capsule.description() == null ||
                capsule.teamWork() == null ||
                capsule.userFileLimit() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
        capsuleService.createCapsule(capsule);
    }

    @Override
    public CapsuleDto getCapsule(String email) {
        return capsuleService.getCapsule(email);
    }
}
