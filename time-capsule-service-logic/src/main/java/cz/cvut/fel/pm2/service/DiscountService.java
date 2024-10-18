package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.CapsuleMapper;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

    private final CapsuleRepository capsuleRepository;

    private final CapsuleMapper capsuleMapper;

    /**
     * Updates an existing discount with the provided DiscountDto details.
     *
     * @param discountDto the DiscountDto containing the updated discount information.
     * @throws NotFoundException if the discount with the specified name is not found.
     */
    public void updateDiscount(@NonNull CapsuleDto discountDto) {

        String discountName = discountDto.name();
        Capsule capsuleEntity = capsuleRepository.getDiscountEntityByName(discountName)
                .orElseThrow(() -> new NotFoundException("Requested discount with name: %s could not be found".formatted(discountName)));
        capsuleMapper.updateEntity(discountDto, capsuleEntity);
        capsuleRepository.save(capsuleEntity);
        log.info("Discount with name: %s was updated".formatted(discountName));
    }
}
