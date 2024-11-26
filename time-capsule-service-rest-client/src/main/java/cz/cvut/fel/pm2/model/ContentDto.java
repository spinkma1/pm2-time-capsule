package cz.cvut.fel.pm2.model;

import cz.cvut.fel.pm2.enums.DataType;

import java.util.Date;

public record ContentDto(
        DataType dataType,
        Date dateOfUpload,
        String name,
        String url,
        byte[] data
) {
}
