package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.persistence.Content;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    /**
     * Converts a ContentEntity to a ContentDto.
     *
     * @param contentEntity the content entity to convert.
     * @return the converted content DTO.
     */
    ContentDto toDto(Content contentEntity);

    /**
     * Converts a ContentDto to a ContentEntity.
     *
     * @param contentDto the content DTO to convert.
     * @return the converted content entity.
     */
    Content toEntity(ContentDto contentDto);
}
