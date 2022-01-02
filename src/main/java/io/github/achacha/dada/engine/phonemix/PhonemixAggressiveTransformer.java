package io.github.achacha.dada.engine.phonemix;

/**
 * A more aggressive variant that remaps consonants and ignores all vowels
 * Better for larger search results
 */
public class PhonemixAggressiveTransformer extends PhonemixTransformerBase {

    PhonemixAggressiveTransformer(PhoneticTransformerBuilder builder) {
        super(builder);
    }

    @Override
    protected void transformOne(char[] s) {
        for (int i = 0; i < s.length; ++i) {
            // Remove vowels and h, does not remove compound vowels
            switch (s[i]) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                case 'h':
                case 'H':
                case 'y':
                case 'Y':
                    if (i == 0 && keepLeadingVowel) {
                        LOGGER.debug(" | Keeping leading vowel (aggressive form), i={} s={}", i, s);
                    }
                    else {
                        LOGGER.debug(" < vowel removal, i={} s={}", i, s);
                        s[i] = NONE;
                        LOGGER.debug(" > ?->_, i={} s={}", i, s);
                    }
                    break;


                case 'q':
                    LOGGER.debug(" < q detected, i={} s={}", i, s);
                    s[i] = 'k';
                    LOGGER.debug(" > q->k, i={} s={}", i, s);
                    break;
            }
        }
    }

}
