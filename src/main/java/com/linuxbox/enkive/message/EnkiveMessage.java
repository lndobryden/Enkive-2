package com.linuxbox.enkive.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.apache.commons.io.FileUtils.writeStringToFile;

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

    private List<String> patch;

    public EnkiveMessage() {

    }

    public String getReconstructedMessage() throws IOException, PatchFailedException {

        File rebuiltMessage = File.createTempFile("EnkiveRebuiltMessage", "eml");

        StringBuilder builder = new StringBuilder();

        writeStringToFile(rebuiltMessage, originalHeaders);
        writeStringToFile(rebuiltMessage, messageBody.reconstructPart(), Charset.defaultCharset(), true);

        List<String> strings = FileUtils.readLines(rebuiltMessage);
        List<String> rebuilt = strings;
        if(patch != null) {
            Patch<String> diffUtilsPatch = DiffUtils.parseUnifiedDiff(patch);
            rebuilt = DiffUtils.patch(strings, diffUtilsPatch);
        }

        return StringUtils.join(rebuilt, IOUtils.LINE_SEPARATOR);
    }
}
