/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package ord.example.words;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Reads a file and generates a summary of word statistics.
 */
public class WordReader {
    private Stream<String> tokens;

    //TODO main in APplication class

    public String read(File file) throws IOException {
        StringBuilder output = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            tokens = scanner.tokens();
            final String wordCount = getWordCount();
            output.append(wordCount);
        }
        return output.toString();
    }

    private String getWordCount() {
        final String wordCount = "Word count = " + tokens.count();
        return wordCount;
    }
}
