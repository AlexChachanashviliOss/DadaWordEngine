package io.github.achacha.dada.engine.base;

import javax.annotation.Nonnull;

public final class WordHelper {
    /**
     * @param c Character assumed lower case
     * @return true if aeiou
     */
    public static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    /**
     * @param c Character assumed lower case
     * @return true if aeiouh
     */
    public static boolean isVowelOrH(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'h';
    }

    /**
     * @return true if is consonant (not a vowel, aeiou) and includes h and y
     */
    public static boolean isConsonant(char c) {
        return !isVowel(c);
    }

    /**
     * Consonants that shorted preceding vowels
     * Voiceless fricatives: f, s, h
     * Voiceless plosives: b, d, g
     *
     * @param c Character assumed lower case
     * @return true if plosive
     */
    public static boolean isPrecedingVowelShortened(char c) {
        return c == 'f' || c == 's' || c == 'h' || c == 'b' || c == 'd' || c == 'g';
    }

    /**
     * Get last character
     *
     * @param word String
     * @return char or \u0000 if empty string
     */
    public static char getLast(@Nonnull String word) {
        if (word.length() > 0)
            return word.charAt(word.length() - 1);
        else
            return '\u0000';
    }

    /**
     * Get character before last
     *
     * @param word String
     * @return char or \u0000 if no such thing
     */
    public static char getOneBeforeLast(@Nonnull String word) {
        if (word.length() > 1)
            return word.charAt(word.length() - 2);
        else
            return '\u0000';
    }

    /**
     * Try to make plural based on pluralization rules
     *
     * @param word String
     * @return String
     */
    @Nonnull
    public static String makePlural(@Nonnull String word) {
        char last = WordHelper.getLast(word);
        String plural;
        switch (last) {
            case 's':
            case 'x':
            case 'z':
                // Add "ES" if verb ends in "S","X","Z"
                plural = word + "es";
                break;

            case 'f':
                // "F" replaced by "VES"
                plural = word.substring(0, word.length() - 1) + "ves";
                break;

            case 'e': {
                // Check for "FE" ending and replace with "VES"
                if (WordHelper.getOneBeforeLast(word) == 'f')
                    plural = word.substring(0, word.length() - 2) + "ves";
                else
                    plural = word + "s";
            }
            break;

            case 'h': {
                char beforeLast = WordHelper.getOneBeforeLast(word);
                if (beforeLast == 'c' || beforeLast == 's')
                    // -CH,-SH = add -ES
                    plural = word + "es";
                else
                    plural = word + "s";
            }
            break;

            case 'y': {
                if (WordHelper.isVowel(WordHelper.getOneBeforeLast(word)))
                    // vowel+Y = add "S"
                    plural = word + "s";
                else
                    // consonant+Y = replace "Y" with "IES"
                    plural = word.substring(0, word.length() - 1) + "ies";
            }
            break;

            case 'o': {
                if (WordHelper.isVowel(WordHelper.getOneBeforeLast(word)))
                    // vowel+O = add "S"
                    plural = word + "s";
                else
                    // consonant+O = add "S"  (in some cases we add "ES" but we pick the more common)
                    plural = word + "s";
            }
            break;

            default:
                plural = word + "s";
        }

        return plural;
    }

    /**
     * Takes a string and returns it with only first letter capitalized
     *
     * @param word String
     * @return String
     * @see org.apache.commons.text.WordUtils
     */
    public static String capitalizeFirstLetter(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
