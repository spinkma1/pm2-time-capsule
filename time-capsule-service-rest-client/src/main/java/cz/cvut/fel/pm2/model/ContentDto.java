package cz.cvut.fel.pm2.model;

import java.util.Date;

/**
 * Data transfer object representing content.
 *
 * @param dataType the type of the content (e.g., image, video, text, etc.)
 * @param dateOfUpload the date when the content was uploaded
 * @param name the name of the content
 * @param url the URL where the content is located
 * @param data the binary data of the content
 */
public record ContentDto(
        String dataType,
        Date dateOfUpload,
        String name,
        String url,
        byte[] data
) {
}
