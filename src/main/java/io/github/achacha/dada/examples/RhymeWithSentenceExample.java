package io.github.achacha.dada.examples;

import io.github.achacha.dada.engine.builder.Sentence;
import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.SavedWord;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Given a word/sentence, try to rhyme with it
 */
public class RhymeWithSentenceExample {
    private final static Logger LOGGER = LogManager.getLogger(RhymeWithSentenceExample.class);

    private static final String PROMPT = "\n\nEnter sentence to try and rhyme with (!q to quit)\n>";

    public static void main(String[] args) {
        LOGGER.info("Resource base path: /data/extended2018");
        GlobalData.loadWordData("resource:/data/extended2018");
        GlobalData.loadHyphenData(GlobalData.DEFAULT_HYPHENDATA_BASE_RESOURCE_PATH);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            System.out.print(PROMPT);
            String input = scanner.nextLine().trim();
            if ("!q".equals(input))
                return;
            if (input.isEmpty())
                continue;

            System.out.println("INPUT : " + input);

            // Get last word to rhyme with
            String rhymeWith = StringUtils.substringAfterLast(input, " ");
            if (rhymeWith.isEmpty())
                rhymeWith = input;

            // Parse input into a sentence and find known words, then convert to a randomized version of it
            Sentence sentence = new Sentence(GlobalData.getWordData()).parse(input);
            System.out.println(sentence.toStringStructure());

            // Validate that we have a sentence with last word that we can rhyme
            SavedWord lastWord = sentence.getWords().get(sentence.getWords().size() - 1);
            if (lastWord.getWord().getType() == Word.Type.Unknown) {
                LOGGER.error("Unknown word: " + lastWord + ", cannot rhyme");
                continue;
            }

            SentenceRendererBuilder randomizedSentence = new SentenceRendererBuilder(sentence);
            System.out.println(randomizedSentence.toStringStructure());

            // Get last word and rhyme and set the rhyme with itself
            randomizedSentence.getRenderers().get(randomizedSentence.getRenderers().size() - 1).setRhymeWith(rhymeWith);
            System.out.println("\nRhymes with:\n");

            for (int i = 0; i < 4; ++i)
                System.out.println(randomizedSentence.execute());
        }
    }
}

