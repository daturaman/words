package org.example.words;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * Reads a file and generates a summary of word statistics.
 */
public class WordReaderService {

    private static final String EXCLUDED_CHARS = "{}[]<>\"':;,!?.";
    private static final String WORD_FILTER_REGEX = "[\\w&@\"'£$%\\-+!?][a-zA-Z0-9]*[\\w\"'.!?+-\\]{1}\\[a-zA-Z0-9]*";
    private List<String> tokens;
    private final ExecutorService executorService;

    public WordReaderService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Reads a plain text file and outputs a summary of the word statistics.
     *
     * @param file a plain text file.
     * @return a summary of the word statistics.
     * @throws IOException there was an error reading the file.
     * @throws InterruptedException an error occurred during the data gathering.
     */
    public String read(File file) throws IOException, InterruptedException, ExecutionException {
        StringBuilder output = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            tokens = scanner.tokens()
                            .map(this::removeLeadingAndTrailingExcludedChars)
                            .filter(wordFilter())
                            .collect(toList());
            final String wordCount = "Word count = " + tokens.size();
            output.append(wordCount);
            final List<Future<String>> results = executorService.invokeAll(
                    List.of(getAverageWordLength(), getWordLengths(), getMostFrequentLengths()));
            for (Future<String> result : results) {
                output.append(result.get());
            }
        }
        return output.toString();
    }

    private Predicate<String> wordFilter() {
        return word -> word.matches(WORD_FILTER_REGEX);
    }

    private Callable<String> getAverageWordLength() {
        return () -> {
            final double averageWordLength = tokens
                    .stream()
                    .mapToInt(String::length)
                    .average()
                    .orElse(0);
            return format("\nAverage word length = %.3f", averageWordLength);
        };
    }

    private Callable<String> getWordLengths() {
        return () -> {
            final Map<Integer, List<String>> wordLengths = mapLengthsToWords();
            return wordLengths
                    .entrySet()
                    .stream()
                    .map(words ->
                            format("\nNumber of words of length %d is %d", words.getKey(), words.getValue().size()))
                    .collect(joining());
        };
    }

    private Callable<String> getMostFrequentLengths() {
        return () -> {
            final Map<Integer, List<String>> wordLengths = mapLengthsToWords();

            TreeMap<Integer, List<String>> map = new TreeMap<>(Comparator.reverseOrder());
            for (Map.Entry<Integer, List<String>> entry : wordLengths.entrySet()) {
                map.computeIfAbsent(entry.getValue().size(), key -> new ArrayList<>(entry.getKey()));
                map.computeIfPresent(entry.getValue().size(), (integer, integers) -> {
                    integers.add(Integer.toString(entry.getKey()));
                    return integers;
                });
            }
            int maxFrequency = map.firstKey();
            String mostFrequentLengths = join(" & ", map.firstEntry().getValue());
            return format("\nThe most frequently occurring word length is %d, for word lengths of %s",
                    maxFrequency,
                    mostFrequentLengths);
        };
    }

    private Map<Integer, List<String>> mapLengthsToWords() {
        return tokens
                .stream()
                .collect(groupingBy(String::length));
    }

    private String removeLeadingAndTrailingExcludedChars(String word) {
        StringBuilder stringBuilder = new StringBuilder(word);
        if (EXCLUDED_CHARS.contains(valueOf(word.charAt(0))) && word.length() > 1) {
            stringBuilder.deleteCharAt(0);
        }
        if (EXCLUDED_CHARS.contains(valueOf(word.charAt(word.length() - 1))) && word.length() > 1) {
            stringBuilder.deleteCharAt(word.length() - 1);
        }
        return stringBuilder.toString();
    }
}
