package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.UserMapper;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final UserService userService; // Pro mapování na DTO
    private final UserMapper userMapper;


    /**
     * Začne sledovat uživatele.
     *
     * @param followerId id sledujícího uživatele
     * @param followedId id sledovaného uživatele
     * @throws NotFoundException pokud některý z uživatelů neexistuje
     * @throws IllegalArgumentException pokud se uživatel pokouší sledovat sám sebe
     */
    @Transactional
    public void followUser(Long followedId, Long followerId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Uživatel nemůže sledovat sám sebe");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("Sledující uživatel nenalezen"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new NotFoundException("Sledovaný uživatel nenalezen"));

        if (!followed.getFollowers().contains(follower)) {
            followed.getFollowers().add(follower);
            userRepository.save(followed);
        }
    }

    /**
     * Přestane sledovat uživatele.
     *
     * @param followerId email sledujícího uživatele
     * @param followedId email sledovaného uživatele
     * @throws NotFoundException pokud některý z uživatelů neexistuje
     */
    @Transactional
    public void unfollowUser(Long followedId, Long followerId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("Sledující uživatel nenalezen"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new NotFoundException("Sledovaný uživatel nenalezen"));

        if (followed.getFollowers().contains(follower)) {
            followed.getFollowers().remove(follower);
            userRepository.save(followed);
        }
    }

    /**
     * Získá seznam sledujících daného uživatele.
     *
     * @param email email uživatele
     * @return seznam sledujících
     * @throws NotFoundException pokud uživatel neexistuje
     */
    public List<UserDto> getFollowers(String email) {
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Uživatel nenalezen"));
        return userMapper.toDtoList(user.getFollowers());
    }

    /**
     * Získá seznam uživatelů, které daný uživatel sleduje.
     *
     * @param email email uživatele
     * @return seznam sledovaných uživatelů
     * @throws NotFoundException pokud uživatel neexistuje
     */
    public List<UserDto> getFollowing(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Uživatel nenalezen"));
        return userMapper.toDtoList(user.getFollowing());
    }

    /**
     * Zkontroluje, zda jeden uživatel sleduje druhého.
     *
     * @param followerEmail email sledujícího
     * @param followedEmail email sledovaného
     * @return true pokud follower sleduje followed
     */
    public boolean isFollowing(String followerEmail, String followedEmail) {
        User follower = userRepository.findByEmail(followerEmail)
                .orElseThrow(() -> new NotFoundException("Sledující uživatel nenalezen"));

        User followed = userRepository.findByEmail(followedEmail)
                .orElseThrow(() -> new NotFoundException("Sledovaný uživatel nenalezen"));

        return followed.getFollowers().contains(follower);
    }


    /**
     * Převede User entitu na UserDto.
     */
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId().longValue(),
                user.getEmail(),
                user.getName(),
                user.getBio(),
                user.getRole().toString(),
                null, // Nebudeme rekurzivně načítat followers
                null  // Nebudeme načítat kapsle
        );
    }
}