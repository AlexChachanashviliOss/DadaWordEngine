package io.github.achacha.dada.examples;

import io.github.achacha.dada.engine.phonemix.PhoneticTransformer;
import io.github.achacha.dada.engine.phonemix.PhoneticTransformerBuilder;

import java.util.Scanner;

/**
 * Given word/sentence, show various phonemix forms
 */
public class PhonemixExample {
    private static final String PROMPT = "\n\nEnter line to convert to phonemix form (!q to quit)\n>";

    private static PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();
    private static PhoneticTransformer transformerIgnoreLeadingVowel = PhoneticTransformerBuilder.builder().withIgnoreLeadingVowel().build();
    private static PhoneticTransformer reverseTransformer = PhoneticTransformerBuilder.builder().withReverse().build();
    private static PhoneticTransformer aggressiveTransformer = PhoneticTransformerBuilder.builder().withAggressiveTransformer().build();
    private static PhoneticTransformer aggressiveReverseTransformer = PhoneticTransformerBuilder.builder().withAggressiveTransformer().withReverse().build();

    public static void main(String[] args) {

        System.out.print(PROMPT);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if ("!q".equals(input))
                return;

            System.out.println("INPUT : "+input);
            System.out.println("\nOUTPUTS\nStandard: "+transformer.transform(input));
            System.out.println("Ignore leading vowel: "+transformerIgnoreLeadingVowel.transform(input));
            System.out.println("Aggressive: "+aggressiveTransformer.transform(input));
            System.out.println("---\nReverse: "+reverseTransformer.transform(input));
            System.out.println("Reverse aggressive: "+aggressiveReverseTransformer.transform(input));

            System.out.print(PROMPT);
        }
    }
}
