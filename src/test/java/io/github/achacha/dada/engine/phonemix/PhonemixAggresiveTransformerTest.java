package io.github.achacha.dada.engine.phonemix;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhonemixAggresiveTransformerTest {
    @Test
    public void testAgggresiveVowelRemoval() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder
                .builder()
                .withAggressiveTransformer()
                .withIgnoreLeadingVowel()
                .build();

        assertEquals("nf", transformer.transform("Enough"));
        assertEquals("ptz", transformer.transform("pizza"));
        assertEquals("k", transformer.transform("aqua"));
        assertEquals("t", transformer.transform("eight"));
    }
}
