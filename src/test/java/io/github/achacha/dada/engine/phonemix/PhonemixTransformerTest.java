package io.github.achacha.dada.engine.phonemix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhonemixTransformerTest {
    @Test
    public void testWords() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();

        assertEquals("hl wrld", transformer.transform("Hello World!"));

        // Words that involve special cases
        assertEquals(transformer.transform("lite"), transformer.transform("light"));
        assertEquals(transformer.transform("fite"), transformer.transform("fight"));
    }

    @Test
    public void testCompoundConsonants() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();
        assertEquals("ptz", transformer.transform("pizza"));
        assertEquals("zr", transformer.transform("there"));
        assertEquals("wr", transformer.transform("where"));
    }

    @Test
    public void testCompoundSoundsWithExplicitVowels() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();

        assertEquals("enOf", transformer.transform("Enough"));
        assertEquals("tOt", transformer.transform("Taught"));
        assertEquals("kOt", transformer.transform("Caught"));
    }

    @Test
    public void testCompoundSoundsWithExplicitVowelsIgnoredLeadingVowel() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder()
                .withIgnoreLeadingVowel()
                .build();

        assertEquals("nOf", transformer.transform("Enough"));
    }

    @Test
    public void testAnomalies() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();
        assertEquals("ak", transformer.transform("aqua "));
    }

    @Test
    public void testLeadingVowelRemoval() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().withIgnoreLeadingVowel().build();
        assertEquals("zs", transformer.transform("This"));  // T should not be removed
        assertEquals("t", transformer.transform("eight"));
    }
}
