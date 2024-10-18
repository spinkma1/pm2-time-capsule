package cz.cvut.fel.pm2.model;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * Data transfer object representing a discount.
 */
public record CapsuleDto (

        Long id,

        String name,

        String description,

        BigDecimal discount,

        LocalDate startDate,

        LocalDate expirationDate
) {

}
