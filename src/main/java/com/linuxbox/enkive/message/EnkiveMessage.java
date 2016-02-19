package com.linuxbox.enkive.message;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 *
 */
@Data
@Document(indexName = "enkivemessages", type = "enkivemessage")
public class EnkiveMessage {

    @Id
    private String id;

    @Field(type = FieldType.String, store = true)
    private String originalHeaders;

    @Field(type = FieldType.Object)
    private EnkiveMessagePart messageBody;

    private String lineSeparator= IOUtils.LINE_SEPARATOR;

    public EnkiveMessage() {

    }

    public String getReconstructedMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(originalHeaders);
        builder.append(lineSeparator);
        builder.append(messageBody.reconstructPart());
        builder.append(lineSeparator);
        return builder.toString();
    }
}
