package io.github.achacha.dada.tools;

import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.engine.data.WordData;
import io.github.achacha.dada.engine.phonemix.PhoneticTransformer;
import io.github.achacha.dada.engine.phonemix.PhoneticTransformerBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.PrintStream;
import java.util.Scanner;

public class PhonemixActionHandler implements BaseActionHandler {
    private final static String PROMPT = "\n'!q', [enter for few random] or word to transform";

    @Override
    public String getHelp() {
        return "REQUIRES: -dataset -phonemix";
    }

    /**
     * Pick random word and apply phonemix transform
     *
     * @param cmd CommandLine with parameters
     * @param out PrintStream for output
     */
    @Override
    public void handle(CommandLine cmd, PrintStream out) {
        String dataset = cmd.getOptionValue("dataset");
        if (dataset == null) {
            out.println("-dataset is required");
            return;
        }
        String phonemix = cmd.getOptionValue("phonemix");
        if (phonemix == null) {
            out.println("-phonemix is required");
            return;
        }

        // Build transformer
        PhoneticTransformerBuilder builder = PhoneticTransformerBuilder.builder();
        final PhoneticTransformerBuilder.TransformerType phonemixType;
        try {
            phonemixType = PhoneticTransformerBuilder.TransformerType.valueOf(phonemix);
        } catch (IllegalArgumentException e) {
            out.println("Invalid phonemix type: `" + phonemix + "`, valid choices: " + ArrayUtils.toString(PhoneticTransformerBuilder.TransformerType.values()));
            out.println(e);
            return;
        }
        out.println("Phonemix: " + phonemixType);
        builder.withTransformer(phonemixType);
        PhoneticTransformer transformer = builder.build();

        // Read word data
        System.out.println("Reading: " + "resource:/data/" + dataset);
        WordData wordData = new WordData("resource:/data/" + dataset);

        Scanner scanner = new Scanner(System.in);
        out.println(PROMPT);
        String input = scanner.nextLine();
        while (!"!q".equalsIgnoreCase(input)) {
            if (input.isEmpty()) {
                for (int i = 0; i < 5; ++i) {
                    final Word.Type wordType;
                    int rnd = RandomUtils.nextInt(0, 100);
                    if (rnd > 50) wordType = Word.Type.Noun;
                    else if (rnd > 10) wordType = Word.Type.Verb;
                    else if (rnd > 4) wordType = Word.Type.Adjective;
                    else wordType = Word.Type.Adverb;

                    Word word = wordData.getRandomWordByType(wordType);
                    out.println("[" + wordType + "] " + word.getWordString() + " -> " + transformer.transform(word.getWordString()) + "\n");
                }
            } else {
                out.println(input + " -> " + transformer.transform(input));
            }

            // prompt
            out.println(PROMPT);
            input = scanner.nextLine();
        }

    }
}
