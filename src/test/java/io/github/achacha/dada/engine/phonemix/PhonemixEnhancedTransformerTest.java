package io.github.achacha.dada.engine.phonemix;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhonemixEnhancedTransformerTest {
    private final PhoneticTransformer transformer = PhoneticTransformerBuilder
            .builder()
            .withEnhancedTransformer()
            .build();

    @Test
    public void testPreprocessorBoundaries() {
        assertEquals("", transformer.transform(""));
        assertEquals("b", transformer.transform("b"));
        assertEquals("wing", transformer.transform("wing"));
        assertEquals("flít", transformer.transform("flight"));
    }

    @Test
    public void testPreprocessor() {
        assertEquals("há", transformer.transform("hay"));
        assertEquals("mábe", transformer.transform("maybe"));
        assertEquals("á", transformer.transform("aye"));
        assertEquals("kàr", transformer.transform("car"));

        // both, bozo, toss, mosh
        assertEquals("böż", transformer.transform("both"));
        assertEquals("böżer", transformer.transform("bother"));
        assertEquals("böß", transformer.transform("boss"));
        assertEquals("möś", transformer.transform("mosh"));

        // a to o
        assertEquals("ol", transformer.transform("all"));
        assertEquals("kol", transformer.transform("call"));

        // long o
        assertEquals("gōd", transformer.transform("good"));
        assertEquals("kœld", transformer.transform("could"));

        // y to i
        assertEquals("líkra", transformer.transform("lycra"));
        assertEquals("lí", transformer.transform("lye"));

        // ee ea
        assertEquals("mët", transformer.transform("meet"));
        assertEquals("mët", transformer.transform("meat"));

        // {vowel}{consonant}e and igh
        assertEquals("lát", transformer.transform("late"));
        assertEquals("rété", transformer.transform("rete"));
        assertEquals("lít", transformer.transform("lite"));
        assertEquals("lít", transformer.transform("light"));
        assertEquals("mút", transformer.transform("mute"));
        assertEquals("kúr", transformer.transform("cure"));
        assertEquals("mús", transformer.transform("muse"));

        // u related changes
        assertEquals("fûr", transformer.transform("fur"));
        assertEquals("pûr", transformer.transform("pour"));
        assertEquals("dœr", transformer.transform("dour"));
    }
}
