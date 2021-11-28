package io.github.achacha.dada.engine.data;

import io.github.achacha.dada.test.GlobalTestData;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordDataTest {
    @Test
    public void testLoadingFallback() {
        WordData wordData = new WordData("does-not-exist");

        assertEquals("resource:/data/default/noun.csv", wordData.getNouns().getResourcePath());
        assertEquals("resource:/data/default/pronoun.csv", wordData.getPronouns().getResourcePath());
        assertEquals("resource:/data/default/verb.csv", wordData.getVerbs().getResourcePath());
        assertEquals("resource:/data/default/adverb.csv", wordData.getAdverbs().getResourcePath());
        assertEquals("resource:/data/default/adjective.csv", wordData.getAdjectives().getResourcePath());
        assertEquals("resource:/data/default/conjunction.csv", wordData.getConjunctions().getResourcePath());
        assertEquals("resource:/data/default/pronoun.csv", wordData.getPronouns().getResourcePath());
    }

    @Test
    public void testLoadingDefault() {
        WordData wordData = new WordData();
        assertEquals("resource:/data/default/noun.csv", wordData.getNouns().getResourcePath());
        assertEquals("resource:/data/default/pronoun.csv", wordData.getPronouns().getResourcePath());
        assertEquals("resource:/data/default/verb.csv", wordData.getVerbs().getResourcePath());
        assertEquals("resource:/data/default/adverb.csv", wordData.getAdverbs().getResourcePath());
        assertEquals("resource:/data/default/adjective.csv", wordData.getAdjectives().getResourcePath());
        assertEquals("resource:/data/default/conjunction.csv", wordData.getConjunctions().getResourcePath());
        assertEquals("resource:/data/default/pronoun.csv", wordData.getPronouns().getResourcePath());
        assertEquals(0, wordData.getIgnore().size());

    }

    @Test
    public void testLoadingTest() {
        WordData wordData = GlobalTestData.WORD_DATA;
        assertEquals("resource:/data/test/", wordData.getBaseResourceDir());
        assertEquals("resource:/data/test/noun.csv", wordData.getNouns().getResourcePath());
        assertEquals("resource:/data/test/pronoun.csv", wordData.getPronouns().getResourcePath());
        assertEquals("resource:/data/test/verb.csv", wordData.getVerbs().getResourcePath());
        assertEquals("resource:/data/test/adverb.csv", wordData.getAdverbs().getResourcePath());
        assertEquals("resource:/data/test/adjective.csv", wordData.getAdjectives().getResourcePath());
        assertEquals("resource:/data/test/conjunction.csv", wordData.getConjunctions().getResourcePath());
        assertEquals("resource:/data/test/pronoun.csv", wordData.getPronouns().getResourcePath());
        assertEquals(1, wordData.getIgnore().size());

    }

    @Test
    public void testFindTextAndWordByType() {
        WordData wordData = new WordData("resource/data/test_parser");
        Optional<SavedWord> pronoun = wordData.findFirstWordsByText("I");

        assertTrue(pronoun.isPresent());
        String formName = pronoun.get().getFormName();
        assertTrue(
                Pronoun.Form.subjective.name().equals(formName) ||
                        Pronoun.Form.personal.name().equals(formName) ||
                        Pronoun.Form.singular.name().equals(formName)
        );
        assertEquals("i", pronoun.get().getWord().getWordString());

        // Make sure we get by type and it's indeed of that type
        assertEquals(Word.Type.Adjective, wordData.getWordsByType(Word.Type.Adjective).getType());
        assertEquals(Word.Type.Adverb, wordData.getWordsByType(Word.Type.Adverb).getType());
        assertEquals(Word.Type.Conjunction, wordData.getWordsByType(Word.Type.Conjunction).getType());
        assertEquals(Word.Type.Noun, wordData.getWordsByType(Word.Type.Noun).getType());
        assertEquals(Word.Type.Preposition, wordData.getWordsByType(Word.Type.Preposition).getType());
        assertEquals(Word.Type.Pronoun, wordData.getWordsByType(Word.Type.Pronoun).getType());
        assertEquals(Word.Type.Verb, wordData.getWordsByType(Word.Type.Verb).getType());
        assertTrue(wordData.getWordsByType(Word.Type.Unknown).wordsData.isEmpty());
    }

    /**
     * Load word sets and check for data issues
     */
    @Test
    public void testLoadingWordDataSets() {
        verifyWordData("resource:/data/default");
        verifyWordData("resource:/data/dada2002");
        verifyWordData("resource:/data/dada2018");
        verifyWordData("resource:/data/extended2018");
    }

    private void verifyWordData(String resourcePath) {
        WordData wordData = new WordData(resourcePath);
        assertFalse(wordData.getAdjectives().isDuplicateFound());
        assertFalse(wordData.getAdverbs().isDuplicateFound());
        assertFalse(wordData.getConjunctions().isDuplicateFound());
        assertFalse(wordData.getNouns().isDuplicateFound());
        assertFalse(wordData.getPrepositions().isDuplicateFound());
        assertFalse(wordData.getVerbs().isDuplicateFound());
    }
}
