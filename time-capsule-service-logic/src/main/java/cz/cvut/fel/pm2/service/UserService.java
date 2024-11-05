package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public List<Capsule> getCapsules(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getCapsules();
    }

    public List<String> getFollowers(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getFollowers().stream().map(User::getEmail).toList();
    }
}
