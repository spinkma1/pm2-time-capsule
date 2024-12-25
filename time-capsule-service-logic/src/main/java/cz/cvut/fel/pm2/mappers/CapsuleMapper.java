package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CapsuleMapper {

//    /**
//     * Updates a CapsuleEntity with values from a CapsuleDto.
//     *
//     * @param capsuleInput  the capsule DTO containing updated values.
//     * @param capsuleEntity the capsule entity to update.
//     */

//    CapsuleDto updateEntity(CapsuleDto capsuleInput,  Capsule capsuleEntity);

    /**
     * Converts a CapsuleEntity to a CapsuleDto.
     *
     * @param capsuleEntity the capsule entity to convert.
     * @return the converted capsule DTO.
     */
    CapsuleDto toDto(Capsule capsuleEntity);

    /**
     * Converts a list of CapsuleEntities to a list of CapsuleDtos.
     *
     * @param capsuleEntities the list of capsule entities to convert.
     * @return the converted list of capsule DTOs.
     */
    List<CapsuleDto> toDtos(List<Capsule> capsuleEntities);

    /**
     * Converts a CapsuleDto to a CapsuleEntity.
     *
     * @param capsuleDto the capsule DTO to convert.
     * @return the converted capsule entity.
     */
    Capsule toEntity(CapsuleDto capsuleDto);
}
