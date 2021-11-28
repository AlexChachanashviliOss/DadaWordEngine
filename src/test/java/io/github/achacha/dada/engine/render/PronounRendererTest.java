package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.base.RendererPredicates;
import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Pronoun;
import io.github.achacha.dada.engine.data.Pronouns;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;
import io.github.achacha.dada.test.GlobalTestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PronounRendererTest {
    @BeforeAll
    public static void beforeClass() {
        GlobalData.setWordData(GlobalTestData.WORD_DATA);
    }

    @Test
    public void testInternals() {
        PronounRenderer tag = new PronounRenderer();
        assertNotNull(tag.toString());
    }

    @Test
    public void testPronounDefaultTag() {
        PronounRenderer tag = new PronounRenderer();
        assertNotNull(tag.execute());
    }

    @Test
    public void testExtendedConstructor() {
        PronounRenderer tag = new PronounRenderer(Pronoun.Form.subjective, ArticleMode.none, CapsMode.first, null);
        assertEquals("Who", tag.execute());
    }

    @Test
    public void testPossessiveRenderer() {
        PronounRenderer tag = new PronounRenderer(Pronoun.Form.possessive, ArticleMode.none, CapsMode.none, null);
        assertEquals("these", tag.execute());
    }

    @Test
    public void testPersonalRenderer() {
        PronounRenderer tag = new PronounRenderer(Pronoun.Form.personal, ArticleMode.none, CapsMode.all, null);
        assertEquals("ME", tag.execute());
    }

    @Test
    public void testPronounRenderer() {
        Pronouns pronouns = new Pronouns("resource:/data/test/");

        PronounRenderer tag = new PronounRenderer(pronouns);
        String pronoun = tag.execute();
        assertNotNull(pronoun);
        assertFalse(pronoun.isEmpty());

        tag.setForm(Pronoun.Form.personal.name());
        assertEquals(Pronoun.Form.personal, tag.getForm());
        assertEquals("me", tag.execute());

        tag.setForm(Pronoun.Form.subjective.name());
        assertEquals(Pronoun.Form.subjective, tag.getForm());
        assertEquals("who", tag.execute());

        tag.setForm(Pronoun.Form.interrogative);
        assertEquals("interrogative", tag.getFormName());
        assertEquals("who", tag.execute());

        tag.setForm(Pronoun.Form.relative.name());
        assertEquals("who", tag.execute());

        tag.setForm(Pronoun.Form.possessive.name());
        assertEquals("these", tag.execute());

        tag.setForm(Pronoun.Form.demonstrative.name());
        assertEquals("these", tag.execute());

        tag.setForm(Pronoun.Form.interrogative.name());
        assertEquals("who", tag.execute());

        tag.setForm(Pronoun.Form.relative.name());
        assertEquals("who", tag.execute());

        tag.setForm(Pronoun.Form.reflexive.name());
        assertEquals("myself", tag.execute());

        tag.setForm(Pronoun.Form.reciprocal.name());
        assertEquals("each other", tag.execute());

        tag.setForm(Pronoun.Form.indefinite.name());
        assertEquals("anybody", tag.execute());

        tag.setForm(Pronoun.Form.plural.name());
        assertEquals("these", tag.execute());
    }

    @Test
    public void testBuilder() {
        SentenceRendererBuilder renderers = new SentenceRendererBuilder();

        String sentence = renderers
                .pronounBuilder()
                    .withArticleMode(ArticleMode.the)
                    .withCapsMode(CapsMode.first)
                    .withRhymeWith("with")
                    .withSaveKey("saved")
                    .withSyllablesDesired(2)
                    .withFallback("NEVER", RendererPredicates.falseAlways())
                    .build()
                .text(" ")
                .pronounBuilder()
                    .withCapsMode(CapsMode.none)
                    .withLoadKey("saved")
                    .withRenderContext(new RenderContextToString<>(GlobalData.getWordData().getPronouns()))
                    .withFallback("NEVER")
                    .withRhymeKey("saved")
                    .build()
                .execute();
        assertEquals("The me me", sentence);

        renderers.pronounBuilder().withForm(Pronoun.Form.possessive);
    }

    @Test
    public void testBuilderAlone() {
        PronounRenderer.Builder builder = PronounRenderer
                .builder(new SentenceRendererBuilder())
                .withForm(Pronoun.Form.reciprocal);
        SentenceRendererBuilder sr = builder.build();

        assertEquals(Pronoun.Form.reciprocal.name(), sr.getRenderers().get(0).getFormName());
        assertEquals(Word.Type.Pronoun, sr.getRenderers().get(0).getType());
        assertEquals("each other", sr.execute());
    }
}
