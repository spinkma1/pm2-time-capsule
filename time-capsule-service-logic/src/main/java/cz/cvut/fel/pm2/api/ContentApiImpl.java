package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentApiImpl implements ContentApi{
    private final ContentService contentService;

    @Override
    public ResponseEntity<ContentDto> uploadContent(Long capsuleId, ContentDto contentDto) {
        return ResponseEntity.ok(contentService.uploadContent(capsuleId, contentDto));
    }
}
