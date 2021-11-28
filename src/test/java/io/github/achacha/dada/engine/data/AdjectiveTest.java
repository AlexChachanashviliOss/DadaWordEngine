package io.github.achacha.dada.engine.data;

import io.github.achacha.dada.engine.phonemix.PhonemixAggressiveTransformer;
import io.github.achacha.dada.engine.phonemix.PhonemixCompactingTransformer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdjectiveTest {
    @Test
    public void testInternals() {
        Adjective adjective = TestWords.makeAdjective("good", "better", "best");
        assertNotNull(adjective.toString());
        assertEquals(Word.Type.Adjective, adjective.getType());

        Adjective adjective2 = TestWords.makeAdjective("good", "better", "best");
        assertEquals(adjective2, adjective);
    }

    @Test
    public void testLoadingAdjective() {
        WordsByType<Adjective> adjectives = new WordsByType<>(Word.Type.Adjective, "resource:/data/test/");

        assertEquals("resource:/data/test/"+ Word.Type.Adjective.getTypeName()+".csv", adjectives.getResourcePath());
        assertEquals(Word.Type.Adjective, adjectives.getType());
        assertEquals(PhonemixCompactingTransformer.class, adjectives.xformer.getClass());        // loaded via config.properties
        assertEquals(PhonemixAggressiveTransformer.class, adjectives.xformerReverse.getClass()); // loaded via config.properties
        assertEquals(1, adjectives.getWordsData().size());

        Adjective adjective = adjectives.getWordsData().get(0);
        assertEquals("subtle", adjective.getWordString());
        assertEquals("more subtle", adjective.getComparative());
        assertEquals("most subtle", adjective.getSuperlative());

    }

    @Test
    public void testToCsv() {
        Adjective adj = TestWords.makeAdjective("small", "somewhat small", "quite small");
        assertEquals("small,somewhat_small,quite_small", adj.toCsv());
    }
}
