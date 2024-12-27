package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContentApiImpl implements ContentApi {
    private final ContentService contentService;

    @Override
    public ResponseEntity<ContentDto> uploadContent(Long capsuleId, ContentDto contentDto) {
        return ResponseEntity.ok(contentService.uploadContent(capsuleId, contentDto));
    }

    @Override
    public ResponseEntity<List<ContentDto>> getAllContent(Long capsuleId) {
        return ResponseEntity.ok(contentService.getAllContent(capsuleId));
    }

    @Override
    public ResponseEntity<ContentDto> updateContent(Long contentId, ContentDto contentDto) {
        return ResponseEntity.ok(contentService.updateContent(contentId, contentDto));
    }

    @Override
    public ResponseEntity<Void> deleteContent(Long contentId) {
        contentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}
