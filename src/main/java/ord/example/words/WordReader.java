/*
 * Copyright 2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package ord.example.words;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Reads a file and generates a summary of word statistics.
 */
public class WordReader {

    private static final String EXCLUDED_CHARS = "{}[]<>\"':;,!?.";
    private static final String ALLOWED_CHARS = "£$%";
    private List<String> tokens;
    private ExecutorService executorService;

    public WordReader(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //TODO main in APplication class

    public String read(File file) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            tokens = scanner.tokens().filter(word -> !word.matches("\\*+")).collect(toList());
            final String wordCount = "Word count = " + tokens.size();
            output.append(wordCount);
            final List<Future<String>> results = executorService.invokeAll(
                    Set.of(getAverageWordLength()));
            for (Future<String> result : results) {
                output.append(result.get());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();//FIXME
        }
        return output.toString();
    }

    private Callable<String> getAverageWordLength() {
        final double averageWordLength = tokens
                .stream()
                .map(this::removeLeadingAndTrailingExcludedChars)
                .mapToInt(String::length)
                .average()
                .orElse(0);
        return () -> String.format("\nAverage word length = %.3f", averageWordLength);
    }

    private String removeLeadingAndTrailingExcludedChars(String word) {
        StringBuilder stringBuilder = new StringBuilder(word);
        if (EXCLUDED_CHARS.contains(String.valueOf(word.charAt(0)))) {
            stringBuilder.deleteCharAt(0);
        }
        if (EXCLUDED_CHARS.contains(String.valueOf(word.charAt(word.length() - 1)))) {
            stringBuilder.deleteCharAt(word.length() - 1);
        }
        return stringBuilder.toString();
    }
}
