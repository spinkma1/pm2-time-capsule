package cz.cvut.fel.pm2.carrentalservice.mappers;

import cz.cvut.fel.pm2.carrentalservice.model.CapsuleDto;
import cz.cvut.fel.pm2.carrentalservice.persistence.CapsuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CapsuleMapper {

    /**
     * Updates a DiscountEntity with values from a DiscountDto.
     *
     * @param capsuleInput the discount DTO containing updated values.
     * @param capsuleEntity the discount entity to update.
     */
    @Mapping(target = "id", ignore = true)
    void updateEntity(CapsuleDto capsuleInput, @MappingTarget CapsuleEntity capsuleEntity);

    /**
     * Converts a DiscountEntity to a DiscountDto.
     *
     * @param capsuleEntity the discount entity to convert.
     * @return the converted discount DTO.
     */
    CapsuleDto toDto(CapsuleEntity capsuleEntity);
}
