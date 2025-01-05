package cz.cvut.fel.pm2.service;

import cz.cvut.fel.pm2.enums.DataType;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        capsule.addContent(content);

        contentRepository.save(content);
        capsuleRepository.save(capsule);

        return contentMapper.toDto(content);
    }

    public List<ContentDto> getAllContent(Long capsuleId) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new NotFoundException("Capsule not found with id: " + capsuleId));

        return capsule.getContents().stream()
                .map(contentMapper::toDto)
                .collect(Collectors.toList());
    }

    public ContentDto updateContent(Long contentId, ContentDto contentDto) {
        Content content = updateContentDetails(contentId, contentDto);

        contentRepository.save(content);

        return contentMapper.toDto(content);
    }

    public void deleteContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("Content not found with id: " + contentId));

        contentRepository.delete(content);
    }

    public Content updateContentDetails(Long contentId, ContentDto contentDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("Content not found with id: " + contentId));

        if (contentDto.dataType() != null) {
            switch (contentDto.dataType().toLowerCase()) {
                case "image":
                    content.setDataType(DataType.IMAGE);
                    break;
                case "video":
                    content.setDataType(DataType.VIDEO);
                    break;
                case "plain_text":
                    content.setDataType(DataType.PLAIN_TEXT);
                    break;
                case "audio":
                    content.setDataType(DataType.AUDIO);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported data type: " + contentDto.dataType());
            }
        }

        if (contentDto.dateOfUpload() != null) {
            content.setDateOfUpload(contentDto.dateOfUpload());
        }

        if (contentDto.name() != null) {
            content.setName(contentDto.name());
        }

        if (contentDto.url() != null) {
            content.setUrl(contentDto.url());
        }

        if (contentDto.data() != null) {
            content.setData(Arrays.copyOf(contentDto.data(), contentDto.data().length));
        }

        return content;
    }

}
