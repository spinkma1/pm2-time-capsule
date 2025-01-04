package cz.cvut.fel.pm2.model;

import java.util.Date;

public record ContentDto(
        String dataType,
        Date dateOfUpload,
        String name,
        String url,
        byte[] data
) {
}
