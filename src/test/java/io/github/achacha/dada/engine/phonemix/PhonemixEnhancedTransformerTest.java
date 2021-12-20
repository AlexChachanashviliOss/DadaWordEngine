package io.github.achacha.dada.engine.phonemix;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhonemixEnhancedTransformerTest {
    @Test
    public void testPreprocessorBoundaries() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder
                .builder()
                .withEnhancedTransformer()
                .build();

        assertEquals("", transformer.transform(""));
        assertEquals("b", transformer.transform("b"));
        assertEquals("wng", transformer.transform("wing"));
        assertEquals("flt", transformer.transform("flight"));
    }

    @Test
    public void testPreprocessor1() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder
                .builder()
                .withEnhancedTransformer()
                .build();

//        assertEquals("", transformer.transform("hay"));
//        assertEquals("", transformer.transform("maybe"));
//        assertEquals("", transformer.transform("aye"));

    }
}
