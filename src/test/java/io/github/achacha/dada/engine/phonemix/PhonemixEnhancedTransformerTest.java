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
        assertEquals("mośon", transformer.transform("motion"));
        assertEquals("konkokśon", transformer.transform("concoction"));
        assertEquals("ßißors", transformer.transform("scissors"));
        assertEquals("trembl", transformer.transform("tremble"));
        assertEquals("mál", transformer.transform("male"));
        assertEquals("récéív", transformer.transform("receive"));
        assertEquals("śëv", transformer.transform("sieve"));
    }

    @Test
    public void testPreprocessorA() {
        assertEquals("há", transformer.transform("hay"));
        assertEquals("mábe", transformer.transform("maybe"));
        assertEquals("á", transformer.transform("aye"));
        assertEquals("kàr", transformer.transform("car"));

        assertEquals("lát", transformer.transform("late"));
        assertEquals("láter", transformer.transform("later"));
        assertEquals("rété", transformer.transform("rete"));
        assertEquals("kàrv", transformer.transform("carve"));
    }

    @Test
    public void testPreprocessorE() {
        // ee ea
        assertEquals("mët", transformer.transform("meet"));
        assertEquals("mët", transformer.transform("meat"));
        assertEquals("óbá", transformer.transform("obey"));
    }

    @Test
    public void testPreprocessorI() {
        // {vowel}{consonant}e and igh
        assertEquals("lít", transformer.transform("lite"));
        assertEquals("lít", transformer.transform("light"));
        assertEquals("mút", transformer.transform("mute"));
        assertEquals("kúr", transformer.transform("cure"));
        assertEquals("mús", transformer.transform("muse"));

        // y to i
        assertEquals("líkra", transformer.transform("lycra"));
        assertEquals("lí", transformer.transform("lye"));
    }

    @Test
    public void testPreprocessorO() {
        // a to o
        assertEquals("ol", transformer.transform("all"));
        assertEquals("kol", transformer.transform("call"));

        // long o
        assertEquals("gōd", transformer.transform("good"));
        assertEquals("kœld", transformer.transform("could"));
        assertEquals("bōty", transformer.transform("booty"));

        // both, bozo, toss, mosh
        assertEquals("böż", transformer.transform("both"));
        assertEquals("böżer", transformer.transform("bother"));
        assertEquals("böß", transformer.transform("boss"));
        assertEquals("möś", transformer.transform("mosh"));

        // with u
        assertEquals("dœr", transformer.transform("dour"));
        assertEquals("dœt", transformer.transform("doubt"));
    }

    @Test
    public void testPreprocessorU() {
        // u related changes
        assertEquals("fûr", transformer.transform("fur"));
        assertEquals("pûr", transformer.transform("pour"));
        assertEquals("büty", transformer.transform("beauty"));
    }

    @Test
    public void testPreprocessorY() {
        // u related changes
        assertEquals("yonder", transformer.transform("yonder"));
    }
}