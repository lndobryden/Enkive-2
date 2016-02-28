package com.linuxbox.enkive.message;

import com.google.common.io.CharStreams;
import com.linuxbox.enkive.TestingConstants;
import difflib.PatchFailedException;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.mail.MessagingException;
import java.io.*;
import java.util.Collection;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.Parameterized.Parameters;

/**
 *
 */
@RunWith(Parameterized.class)
public class MessageParsingTest {


    private File testFile;
    private static EnkiveMessageParser parser;

    public MessageParsingTest(File file) {
        this.testFile = file;
    }

    @Parameters
    public static Collection<File> data() {
        return FileUtils.listFiles(
                new File(TestingConstants.TEST_MESSAGE_DIRECTORY), null, true);
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        parser = new EnkiveMessageParser();
    }


    @Test
    public void parseMessage() throws IOException, MessagingException, PatchFailedException {

        FileInputStream inputStream = new FileInputStream(testFile);
        EnkiveMessage testMessage = parser.constructMessage(new BufferedInputStream(inputStream));
        inputStream.close();

        FileReader equalityReader = new FileReader(testFile);
        String originalMessage = CharStreams.toString(equalityReader);
        equalityReader.close();

        assertThat("File did not match: " + testFile.getName(), testMessage.getReconstructedMessage(), is(equalToIgnoringWhiteSpace(originalMessage)));

    }

}
