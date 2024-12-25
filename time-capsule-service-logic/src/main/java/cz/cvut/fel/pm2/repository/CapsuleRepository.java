package cz.cvut.fel.pm2.repository;

import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    /**
     * Find capsule by name
     * @param name name of the capsule
     * @return capsule with the given name
     */
    Optional<Capsule> getCapsuleByName(String name);

    /**
     * Find capsule by owner
     * @param owner owner of the capsule
     * @return capsule with the given owner
     */
    Optional<List<Capsule>> getCapsulesByOwner(User owner);


    /**
     * Find capsule by id
     * @param id id of the capsule
     * @return capsule with the given id
     */
    Optional<Capsule> getCapsuleById(Long id);

    /**
     * Find capsule by qr code password
     * @param password qr code password
     * @return capsule with the given qr code password
     */
    Optional<Capsule> findByQrCodePassword(String password);

    /**
     * Finds capsules with an unlock time between the specified start and end times.
     *
     * @param startTime the starting point of the unlock time range.
     * @param endTime the ending point of the unlock time range.
     * @return a list of capsules whose unlock time falls between the given range.
     */
    List<Capsule> findByUnlockTimeBetween(LocalDateTime startTime, LocalDateTime endTime);


}
