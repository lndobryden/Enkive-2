package com.linuxbox.enkive.message;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lee on 1/27/16.
 */
@Data
@JsonTypeName("multi")
public class EnkiveMessagePartMulti extends EnkiveMessagePart {

    @Field(type = FieldType.String, store = true)
    private String preamble;
    @Field(type = FieldType.String, store = true)
    private String epilogue;
    @Field(type = FieldType.String, store = true)
    private String boundary;
    @Field(type = FieldType.String, store = true)
    private String originalHeaders;
    @Field(type = FieldType.String, store = true)
    private String lineEnding = IOUtils.LINE_SEPARATOR;

    @Field(type = FieldType.Nested)
    private List<EnkiveMessagePart> messageParts;

    public EnkiveMessagePartMulti() {
        messageParts = new LinkedList<EnkiveMessagePart>();
    }

    public void addMessagePart(EnkiveMessagePart mp) {
        messageParts.add(mp);
    }

    @Override
    public String reconstructPart() {
        StringBuilder builder = new StringBuilder();
        builder.append(originalHeaders);
        builder.append(lineEnding);
        builder.append(preamble);
        builder.append(lineEnding);

        for (EnkiveMessagePart part : messageParts) {
            builder.append("--" + boundary + lineEnding);
            builder.append(part.reconstructPart() + lineEnding);
        }
        builder.append("--" + boundary + "--" + lineEnding);
        builder.append(epilogue + lineEnding);

        return builder.toString();
    }
}
