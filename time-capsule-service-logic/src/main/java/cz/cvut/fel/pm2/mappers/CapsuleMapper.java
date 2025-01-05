package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.UnlockMethodState;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import cz.cvut.fel.pm2.model.CapsuleDto;
import cz.cvut.fel.pm2.model.UnlockMethodsDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CapsuleMapper {

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

    // Helper metody pro mapování UnlockMethods
    default UnlockMethodsDto mapUnlockMethodsToDto(Map<UnlockMethod, UnlockMethodState> unlockMethods) {
        if (unlockMethods == null) {
            return new UnlockMethodsDto(false, false, false, false, false, false, false, false);
        }

        UnlockMethodState timeState = unlockMethods.getOrDefault(UnlockMethod.TIME, new UnlockMethodState());
        UnlockMethodState qrCodeState = unlockMethods.getOrDefault(UnlockMethod.QR_CODE, new UnlockMethodState());
        UnlockMethodState geolocationState = unlockMethods.getOrDefault(UnlockMethod.GEOLOCATION, new UnlockMethodState());
        UnlockMethodState passwordState = unlockMethods.getOrDefault(UnlockMethod.PASSWORD, new UnlockMethodState());

        return new UnlockMethodsDto(
                timeState.isEnabled(),
                timeState.isComplete(),
                qrCodeState.isEnabled(),
                qrCodeState.isComplete(),
                geolocationState.isEnabled(),
                geolocationState.isComplete(),
                passwordState.isEnabled(),
                passwordState.isComplete()
        );
    }

    default Map<UnlockMethod, UnlockMethodState> mapUnlockMethodsToEntity(UnlockMethodsDto dto) {
        if (dto == null) {
            return new HashMap<>();
        }

        Map<UnlockMethod, UnlockMethodState> unlockMethods = new HashMap<>();
        unlockMethods.put(UnlockMethod.TIME, new UnlockMethodState(dto.timeEnabled(), dto.timeComplete()));
        unlockMethods.put(UnlockMethod.QR_CODE, new UnlockMethodState(dto.qrCodeEnabled(), dto.qrCodeComplete()));
        unlockMethods.put(UnlockMethod.GEOLOCATION, new UnlockMethodState(dto.geolocationEnabled(), dto.geolocationComplete()));
        unlockMethods.put(UnlockMethod.PASSWORD, new UnlockMethodState(dto.passwordEnabled(), dto.passwordComplete()));

        return unlockMethods;
    }
}
