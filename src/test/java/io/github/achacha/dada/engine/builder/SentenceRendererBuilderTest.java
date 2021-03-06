package io.github.achacha.dada.engine.builder;

import io.github.achacha.dada.engine.data.Verb;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.engine.data.WordData;
import io.github.achacha.dada.integration.tags.GlobalData;
import io.github.achacha.dada.test.GlobalTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SentenceRendererBuilderTest {
    @Test
    public void testConversionFromSentence() {
        Sentence sentence = new Sentence(GlobalTestData.WORD_DATA)
                .parse("noun ADVERBLY swum & swim me noun");
        assertEquals(13, sentence.getWords().size());

        SentenceRendererBuilder renderers = new SentenceRendererBuilder(sentence);
        renderers.setWordData(new WordData(GlobalData.DADA2018_WORDDATA_BASE_RESOURCE_PATH));
        assertEquals(13, renderers.getRenderers().size());

        assertTrue(!renderers.execute().isEmpty());
    }

    @Test
    public void testRhymingTwoWords() {
        SentenceRendererBuilder sr = new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                .verbBuilder()
                    .withRhymeWith("napping")
                    .withForm(Verb.Form.present)
                    .build()
                .text(", ")
                .verbBuilder()
                    .withRhymeWith("rapping")
                    .withSaveKey("rapping")
                    .withForm(Verb.Form.present)
                    .build()
                .text(", ")
                .verbBuilder()
                    .withRhymeWith("rapping")
                    .withLoadKey("rapping")
                    .withForm(Verb.Form.present)
                    .build()
                .text(" ")
                .verbBuilder()
                    .withRhymeWith("rapping")
                    .withLoadKey("rapping")
                    .withForm(Verb.Form.present)
                    .build();

        assertEquals("swimming, swimming, swimming swimming", sr.execute());  // test only has 1 word
    }

    @Test
    public void testSpacingOnExecute() {
        assertEquals("swimnoun",
                new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                .verb()
                .text("")
                .noun()
                .execute());

        assertEquals("swimnoun",
                new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                        .verb()
                        .noun()
                        .execute());

        assertEquals("swim noun",
                new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                        .verb()
                        .text(" ")
                        .noun()
                        .execute());

        assertEquals("hellothere",
                new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                        .text("hello")
                        .text("there")
                        .execute());

        assertEquals("Hello noun there!",
                new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                        .text("Hello ")
                        .noun()
                        .text(" there!")
                        .execute());
    }

    @Test
    public void testAllBuilders() {
        SentenceRendererBuilder sr = new SentenceRendererBuilder(GlobalTestData.WORD_DATA)
                .nounBuilder().build()
                .text(" ")
                .verbBuilder().build()
                .text(" ")
                .pronounBuilder().build()
                .text(" ")
                .prepositionBuilder().build()
                .text(" ")
                .conjunctionBuilder().build()
                .text(" ")
                .adverbBuilder().build()
                .text(" ")
                .adjectiveBuilder().build();

        assertEquals("noun swim me on & adverbly subtle", sr.execute());
        assertEquals(Word.Type.Noun, sr.getRenderers().get(0).getType());
        assertEquals(Word.Type.Verb, sr.getRenderers().get(2).getType());
        assertEquals(Word.Type.Pronoun, sr.getRenderers().get(4).getType());
        assertEquals(Word.Type.Preposition, sr.getRenderers().get(6).getType());
        assertEquals(Word.Type.Conjunction, sr.getRenderers().get(8).getType());
        assertEquals(Word.Type.Adverb, sr.getRenderers().get(10).getType());
        assertEquals(Word.Type.Adjective, sr.getRenderers().get(12).getType());
    }
}
