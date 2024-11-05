package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
