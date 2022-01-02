package io.github.achacha.dada.examples;

import io.github.achacha.dada.engine.data.SavedWord;
import io.github.achacha.dada.engine.data.WordData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Given a noun, find other nouns that have a phonetic rhyme to it
 */
public class FindWordsThatRhymeExample {
    private final static Logger LOGGER = LogManager.getLogger(FindWordsThatRhymeExample.class);

    private static final String PROMPT = "\n\nEnter noun to try and rhyme (!q to quit)\n>";

    public static void main(String[] args) {
        LOGGER.info("Resource base path: /data/extended2018");
        WordData wordData = new WordData("resource:/data/extended2018");

        System.out.print(PROMPT);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if ("!q".equals(input))
                return;

            System.out.println("INPUT : " + input);

            List<SavedWord> wordsMatched = wordData.getNouns().findRhymes(input);
            if (wordsMatched.size() > 0) {
                    List<String> allMatched = wordsMatched.stream()
                            .map(SavedWord::getWordString)   // Get the word form that was used
                            .collect(Collectors.toList());
                    Collections.shuffle(allMatched);

                    System.out.println("Rhymed nouns\n---\n"+allMatched
                            .stream()
                            .limit(10)
                            .collect(Collectors.joining("\n")));
            } else {
                System.out.println("No rhyming word found for word=`" + input);
            }

            System.out.print(PROMPT);
        }
    }
}

