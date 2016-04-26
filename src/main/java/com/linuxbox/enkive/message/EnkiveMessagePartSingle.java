package com.linuxbox.enkive.message;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by Lee on 1/28/16.
 */
@Data
@JsonTypeName("single")
public class EnkiveMessagePartSingle extends EnkiveMessagePart {

    @Field(type = FieldType.Attachment, store = true, searchAnalyzer = "english")
    private String content;
    @Field(type = FieldType.String, store = true)
    private String contentTransferEncoding;
    @Field(type = FieldType.String, store = true)
    private String originalHeaders;

    private String lineEnding= IOUtils.LINE_SEPARATOR;

    @Override
    public String reconstructPart() {
        StringBuilder builder = new StringBuilder();
        builder.append(originalHeaders + lineEnding);
        builder.append(content + lineEnding);
        return builder.toString();
    }

}
