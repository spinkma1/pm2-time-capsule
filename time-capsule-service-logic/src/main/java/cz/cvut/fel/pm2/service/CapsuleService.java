package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.UnlockMethod;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepository;

    private final CapsuleMapper capsuleMapper;
    private final UserRepository userRepository;

    public void createCapsule(@NonNull CapsuleDto capsuleDto) {
        Capsule capsule = capsuleMapper.toEntity(capsuleDto);
        capsuleRepository.save(capsule);
    }

    public CapsuleDto getCapsule(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return capsuleRepository.getCapsulesByOwner(user)
                .map(capsuleMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Capsule not found"));
    }

    public void readyCapsule(String capsuleId, boolean ready) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        if(ready) {
            capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setState(State.WAIT);
        }
        else {
            capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setState(State.EDIT);
        }
        capsuleRepository.save(capsule.get());
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
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockMethods(methodSet);
        capsuleRepository.save(capsule.get());
    }

    public void setCapsuleTime(String capsuleId, LocalDateTime time) {
        var capsule = capsuleRepository.getCapsuleByName(capsuleId);
        capsule.orElseThrow(() -> new NotFoundException("Capsule not found")).setUnlockTime(time);
        capsuleRepository.save(capsule.get());
    }

}
