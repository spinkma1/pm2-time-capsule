package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for content-related API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class ContentApiImpl implements ContentApi {
    private final ContentService contentService;

    /**
     * Uploads content to a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param contentDto the content data transfer object
     * @return the uploaded content data transfer object
     */
    @Override
    public ResponseEntity<ContentDto> uploadContent(Long capsuleId, ContentDto contentDto) {
        return ResponseEntity.ok(contentService.uploadContent(capsuleId, contentDto));
    }

    /**
     * Retrieves all content for a capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return a list of content data transfer objects
     */
    @Override
    public ResponseEntity<List<ContentDto>> getAllContent(Long capsuleId) {
        return ResponseEntity.ok(contentService.getAllContent(capsuleId));
    }

    /**
     * Updates content.
     *
     * @param contentId the ID of the content
     * @param contentDto the content data transfer object
     * @return the updated content data transfer object
     */
    @Override
    public ResponseEntity<ContentDto> updateContent(Long contentId, ContentDto contentDto) {
        return ResponseEntity.ok(contentService.updateContent(contentId, contentDto));
    }

    /**
     * Deletes content.
     *
     * @param contentId the ID of the content
     * @return a response entity with no content
     */
    @Override
    public ResponseEntity<Void> deleteContent(Long contentId) {
        contentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}
