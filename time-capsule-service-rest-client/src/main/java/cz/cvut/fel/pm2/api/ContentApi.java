package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.ContentDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;

@Tag(name = "Content API", description = "API for managing content within capsules.")
@RequestMapping("/content")
public interface ContentApi {

    /**
     * Uploads content to a specific capsule.
     *
     * @param capsuleId the ID of the capsule
     * @param contentDto the content data transfer object
     * @return the uploaded content data transfer object
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/upload/{capsuleId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Upload content to specific capsule")
    @ResponseBody
    ResponseEntity<ContentDto> uploadContent(
            @PathVariable Long capsuleId,
            @RequestBody ContentDto contentDto
    );

    /**
     * Retrieves all content for a specific capsule.
     *
     * @param capsuleId the ID of the capsule
     * @return a list of content data transfer objects
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{capsuleId}", produces = "application/json")
    @Operation(summary = "Get all content for a specific capsule")
    @ResponseBody
    ResponseEntity<List<ContentDto>> getAllContent(@PathVariable Long capsuleId);

    /**
     * Updates content by ID.
     *
     * @param contentId the ID of the content
     * @param contentDto the content data transfer object
     * @return the updated content data transfer object
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/update/{contentId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update content by ID")
    @ResponseBody
    ResponseEntity<ContentDto> updateContent(@PathVariable Long contentId, @RequestBody ContentDto contentDto);

    /**
     * Deletes content by ID.
     *
     * @param contentId the ID of the content
     * @return a response entity with no content
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/delete/{contentId}")
    @Operation(summary = "Delete content by ID")
    @ResponseBody
    ResponseEntity<Void> deleteContent(@PathVariable Long contentId);
}