package cz.cvut.fel.pm2.carrentalservice.repository;

import cz.cvut.fel.pm2.carrentalservice.persistence.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    /**
     * Retrieves a DiscountEntity by its name.
     *
     * @param name the name of the discount entity.
     * @return an Optional containing the discount entity, if found.
     */
    Optional<Capsule> getDiscountEntityByName(String name);
}
