package cz.cvut.fel.pm2.mappers;

import cz.cvut.fel.pm2.enums.DataType;
import cz.cvut.fel.pm2.model.ContentDto;
import cz.cvut.fel.pm2.persistence.Content;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Primary
@Component
public class ContentMapperImp implements ContentMapper {

    @Override
    public ContentDto toDto(Content contentEntity) {
        if ( contentEntity == null ) {
            return null;
        }

        String dataType = null;
        Date dateOfUpload = null;
        String name = null;
        String url = null;
        byte[] data = null;

        if ( contentEntity.getDataType() != null ) {
            switch (contentEntity.getDataType()) {
                case IMAGE:
                    dataType = "image";
                    break;
                case VIDEO:
                    dataType = "video";
                    break;
                case PLAIN_TEXT:
                    dataType = "text";
                    break;
                case AUDIO:
                    dataType = "audio";
                    break;
                case PDF:
                    dataType = "pdf";
                    break;
            }
        }
        dateOfUpload = contentEntity.getDateOfUpload();
        name = contentEntity.getName();
        url = contentEntity.getUrl();
        byte[] data1 = contentEntity.getData();
        if ( data1 != null ) {
            data = Arrays.copyOf( data1, data1.length );
        }

        ContentDto contentDto = new ContentDto( dataType, dateOfUpload, name, url, data );

        return contentDto;
    }

    @Override
    public Content toEntity(ContentDto contentDto) {
        if (contentDto == null) {
            return null;
        }

        Content content = new Content();

        if (contentDto.dataType() != null) {
            switch (contentDto.dataType().toLowerCase()) {
                case "image":
                    content.setDataType(DataType.IMAGE);
                    break;
                case "video":
                    content.setDataType(DataType.VIDEO);
                    break;
                case "text":
                    content.setDataType(DataType.PLAIN_TEXT);
                    break;
                case "audio":
                    content.setDataType(DataType.AUDIO);
                    break;
                case "pdf":
                    content.setDataType(DataType.PDF);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported data type: " + contentDto.dataType());
            }
        }

        content.setDateOfUpload(contentDto.dateOfUpload());
        content.setName(contentDto.name());
        content.setUrl(contentDto.url());

        byte[] data = contentDto.data();
        if (data != null) {
            content.setData(Arrays.copyOf(data, data.length));
        }

        return content;
    }

}
