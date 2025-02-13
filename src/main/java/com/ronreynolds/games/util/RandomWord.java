package com.ronreynolds.games.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RandomWord {
    // keep the word sizes reasonable
    private static final int minWordLength = 5;
    private static final int maxWordLength = 15;
    private static final Path[] WORD_FILES = {
            Path.of("src/main/resources/words.txt"), // when run from within the IDE
            Path.of("words.txt"),                   // when run from the command-line
    };
    private static final List<String> words = new ArrayList<>();
    private static int numWords;

    static {
        for (Path wordFile : WORD_FILES) {
            try {
                Files.readAllLines(wordFile)
                        .stream()
                        .filter(word -> word.length() >= minWordLength && word.length() <= maxWordLength)
                        .filter(RandomWord::onlyLetters)
                        .forEach(words::add);
                numWords = words.size();
                log.info("loaded {} words from {}", numWords, wordFile);
                break;  // only read 1 file
            } catch (IOException readFailure) {
                log.info("failure to read file {}", wordFile, readFailure);
            }
        }
        // if we failed to load any words then we can't run the game :(
        if(words.isEmpty()) {
            throw new IllegalStateException("failed to load a word file :(");
        }
    }

    private static boolean onlyLetters(String word) {
        return word.matches("[a-zA-Z]+");    // word ONLY contains letters (no digits, no spaces, no dashes, etc)
    }

    /**
     * @return a random valid english word
     */
    public static String newWord() {
        return words.get(ThreadLocalRandom.current().nextInt(numWords));
    }
}
