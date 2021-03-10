package org.example.words;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main entry point for running the word service as a standalone application.
 */
public class Application {

    /**
     * Accepts an array of one or more file paths that are either absolute or relative to the place of execution.
     *
     * @param filePaths paths to locate text files.
     */
    public static void main(String[] filePaths) {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        final WordReaderService wordReaderService = new WordReaderService(executorService);
        try {
            for (String filePath : filePaths) {
                System.out.println("\nWords statistics for " + filePath + "\n");
                final File file = new File(filePath);
                System.out.println(wordReaderService.read(file));
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        executorService.shutdown();
    }
}
