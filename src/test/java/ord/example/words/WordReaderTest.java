package ord.example.words;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class WordReaderTest {


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @MethodSource("testFiles")
    void read(String testFile, String expectedFile) throws URISyntaxException, IOException {
        final WordReader wordReader = new WordReader(Executors.newCachedThreadPool());
        File file = new File(getClass().getResource(testFile).toURI());
        final String actualOutput = wordReader.read(file);
        final String expectedOutput = expectedFile(expectedFile);
        assertEquals(expectedOutput, actualOutput);
    }

    private String expectedFile(String fileName) throws URISyntaxException, IOException {
        try (Stream<String> lines = Files.lines(Path.of(getClass().getResource(fileName).toURI()))) {
            return lines.collect(Collectors.joining("\n"));
        }
    }

    private static Stream<Arguments> testFiles() {
        return Stream.of(
                Arguments.of("/test1.txt", "/test1_expected.txt"),
                Arguments.of("/test2.txt", "/test2_expected.txt")
        );

    }
}