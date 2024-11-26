package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.mappers.ContentMapper;
import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.Content;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final CapsuleRepository capsuleRepository;
    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    public ContentDto uploadContent(Long capsuleId, ContentDto contentDto) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found with id: " + capsuleId));

        Content content = contentMapper.toEntity(contentDto);
        content.setCapsule(capsule);

        return contentMapper.toDto(contentRepository.save(content));
    }
}
