package ord.example.words;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WordReaderTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void read() throws URISyntaxException {
        final WordReader wordReader = new WordReader();
        File file = new File(getClass().getResource("/test1.txt").toURI());
        assertNotNull(file);
    }
}