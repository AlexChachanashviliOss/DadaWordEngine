package io.github.achacha.dada.engine.data;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordsByTypeTest {
    private WordsByType<Verb> verbs = new WordsByType<>(Word.Type.Verb, "resource:/data/test/");

    @Test
    public void testInternals() {
        assertNotNull(verbs.getXformer());
        assertNotNull(verbs.getXformerReverse());
        assertNotNull(verbs.getWordsByPhonetic());
        assertNotNull(verbs.getWordBucketsByReversePhonetic());
        assertNotNull(verbs.getWordsData());
        assertFalse(verbs.toString().isEmpty());
    }

    @Test
    public void testLoadingVerb() {
        assertEquals("resource:/data/test/"+ Word.Type.Verb.getTypeName()+".csv", verbs.getResourcePath());
        assertEquals(Word.Type.Verb, verbs.getType());
        assertEquals(1, verbs.getWordsData().size());

        Verb verb = verbs.getWordsData().get(0);
        assertEquals("swim", verb.getWordString());
        assertEquals("swam", verb.getPast());
        assertEquals("swum", verb.getPastParticiple());
        assertEquals("swims", verb.getSingular());
        assertEquals("swimming", verb.getPresent());
        assertEquals("to swim", verb.getInfinitive());
    }

    @Test
    public void testLoadingAdverb() {
        WordsByType<Adverb> adverbs = new WordsByType<>(Word.Type.Adverb, "resource:/data/test/");

        assertEquals("resource:/data/test/"+ Word.Type.Adverb.getTypeName()+".csv", adverbs.getResourcePath());
        assertEquals(Word.Type.Verb, verbs.getType());
        assertEquals(1, adverbs.getWordsData().size());

        Adverb adverb = adverbs.getWordsData().get(0);
        assertEquals("adverbly", adverb.getWordString());
    }

    @Test
    public void testAllForms() {
        // There are 5 forms to swim (excluding infinitive)
        assertEquals(5, verbs.wordsByText.size());
    }

    /**
     * Process test data and generate the withReverse phonetic -> word multimap
     */
    @Test
    public void testPhonetics() {
        assertEquals(5, verbs.wordsByPhonetic.size());
    }

    @Test
    public void testReversePhonetics() {
        Map<String, Set<SavedWord>> bucket = verbs.wordBucketsByReversePhonetic.get('m');
        assertNotNull(bucket);
        assertEquals(3, bucket.size());
    }

    @Test
    public void testSave() {
        WordData wordData = new WordData();

        wordData.getWordsByTypeStream().forEach(this::saveWordsAndVerify);
    }

    private void saveWordsAndVerify(WordsByType<? extends Word> wordByType) {
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        try {
            wordByType.writeWords(writer);
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to write", e);
        }
        String result = sw.toString();

        String outputHeader = wordByType.getOutputHeader();
        assertTrue(result.startsWith(outputHeader));

        int count = StringUtils.countMatches(result, "\n");
        assertEquals(wordByType.wordsData.size()+1, count);
    }

}
