package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Pronoun;
import io.github.achacha.dada.engine.data.Pronouns;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class PronounRenderer extends BaseWordRenderer<Pronoun> {
    protected Pronoun.Form form = Pronoun.Form.personal;

    public PronounRenderer() {
        super(new RenderContextToString<>(GlobalData.getWordData().getPronouns()));
    }

    /**
     * Custom render context
     * @param renderContext {@link RenderContext}
     */
    public PronounRenderer(RenderContext<Pronoun> renderContext) {
        super(renderContext);
    }

    /** Used in testing */
    PronounRenderer(Pronouns pronouns) {
        super(new RenderContextToString<>(pronouns));
    }

    /**
     * Extended constructor
     * @param form {@link Pronoun.Form}
     * @param articleMode {@link ArticleMode}
     * @param capsMode {@link CapsMode}
     * @param renderContext {@link RenderContext} or null to use {@link RenderContextToString} with GlobalData
     */
    public PronounRenderer(Pronoun.Form form, ArticleMode articleMode, CapsMode capsMode, RenderContext<Pronoun> renderContext) {
        super(
                renderContext == null ? new RenderContextToString<>(GlobalData.getWordData().getPronouns()) : renderContext,
                articleMode,
                capsMode
        );
        this.form = form;
    }

    @Override
    public Word.Type getType() {
        return Word.Type.Pronoun;
    }

    /**
     * Builder to be used with SentenceRendererBuilder
     * @param sentenceBuilder SentenceRendererBuilder
     * @return Builder
     */
    public static Builder builder(SentenceRendererBuilder sentenceBuilder) {
        return new Builder(sentenceBuilder);
    }

    public static class Builder {
        private final SentenceRendererBuilder sentenceBuilder;
        private Pronoun.Form form = Pronoun.Form.personal;
        private ArticleMode articleMode = ArticleMode.none;
        private CapsMode capsMode = CapsMode.none;
        private RenderContext<Pronoun> renderContext;
        private String loadKey;
        private String saveKey;
        private String rhymeKey;
        private String rhymeWith;
        private int syllablesDesired;
        private String fallback;
        private Predicate<BaseWordRenderer<Pronoun>> fallbackPredicate;

        public Builder(SentenceRendererBuilder sentenceBuilder) {
            this.sentenceBuilder = sentenceBuilder;
        }

        /**
         * Add to provided SentenceRendererBuilder
         * @return {@link SentenceRendererBuilder} provided in constructor
         */
        public SentenceRendererBuilder build() {
            PronounRenderer renderer = new PronounRenderer(form, articleMode, capsMode, renderContext);
            renderer.loadKey = loadKey;
            renderer.saveKey = saveKey;
            renderer.rhymeKey = rhymeKey;
            renderer.rhymeWith = rhymeWith;
            renderer.syllablesDesired = syllablesDesired;
            renderer.fallback = fallback;
            renderer.fallbackPredicate = fallbackPredicate;

            // Validate and add
            validateRenderer(renderer);
            sentenceBuilder.getRenderers().add(renderer);

            return sentenceBuilder;
        }

        public Builder withForm(Pronoun.Form form) {
            this.form = form;
            return this;
        }

        public Builder withArticleMode(ArticleMode articleMode) {
            this.articleMode = articleMode;
            return this;
        }

        public Builder withCapsMode(CapsMode capsMode) {
            this.capsMode = capsMode;
            return this;
        }

        public Builder withRenderContext(RenderContext<Pronoun> renderContext) {
            this.renderContext = renderContext;
            return this;
        }

        public Builder withLoadKey(String loadKey) {
            this.loadKey = loadKey;
            return this;
        }

        public Builder withSaveKey(String saveKey) {
            this.saveKey = saveKey;
            return this;
        }

        public Builder withRhymeKey(String rhymeKey) {
            this.rhymeKey = rhymeKey;
            return this;
        }

        public Builder withRhymeWith(String rhymeWith) {
            this.rhymeWith = rhymeWith;
            return this;
        }

        public Builder withSyllablesDesired(int syllablesDesired) {
            this.syllablesDesired = syllablesDesired;
            return this;
        }

        public Builder withFallback(String fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder withFallback(String fallback, Predicate<BaseWordRenderer<Pronoun>> fallbackPredicate) {
            this.fallback = fallback;
            this.fallbackPredicate = fallbackPredicate;
            return this;
        }
    }

    @Override
    protected Word generateWord() {
        Pronouns pronouns = (Pronouns) rendererContext.getWords();
        try {
            Word word = pronouns.getRandomPronounByForm(form);
            LOGGER.debug("Generated pronoun={} form={}", word, form.name());
            return word;
        }
        catch(IllegalArgumentException e) {
            LOGGER.debug("Skipping unknown form `{}` in {}", form, this);
        }
        return super.generateWord();
    }

    public Pronoun.Form getForm() {
        return form;
    }

    public void setForm(Pronoun.Form form) {
        this.form = form;
    }

    @Override
    public String getFormName() {
        return form.name();
    }

    @Override
    public void setForm(String formName) {
        if (StringUtils.isNotEmpty(formName)) {
            try {
                this.form = Pronoun.Form.valueOf(StringUtils.trim(formName).toLowerCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid form name for this={} formName={}", this, formName);
            }
        }
    }
}
