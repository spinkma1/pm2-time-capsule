package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscountApiImpl implements CapsuleApi {

    private final DiscountService discountService;

    /**
     * Updates capsule parameters.
     *
     * @param capsule the discount data to update
     */
    @Override
    public void updateDiscount(CapsuleDto capsule) {
        if (capsule.discount() == null || capsule.name() == null || capsule.description() == null || capsule.id() == null) {
            throw new InvalidBodyException("No or wrong body was sent");
        }
        discountService.updateDiscount(capsule);
    }
}
