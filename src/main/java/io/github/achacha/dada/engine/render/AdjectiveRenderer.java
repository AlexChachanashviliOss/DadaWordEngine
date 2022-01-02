package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Adjective;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class AdjectiveRenderer extends BaseWordRenderer<Adjective> {

    protected Adjective.Form form = Adjective.Form.positive;

    /**
     * Build renderer with string based context
     * @see RenderContextToString
     */
    public AdjectiveRenderer() {
        super(new RenderContextToString<>(GlobalData.getWordData().getAdjectives()));
    }

    /**
     * Build renderer with custom context
     * @param renderContext {@link RenderContext}
     */
    public AdjectiveRenderer(RenderContext<Adjective> renderContext) {
        super(renderContext);
    }

    /**
     * Extended constructor
     * @param form {@link Adjective.Form}
     * @param articleMode {@link ArticleMode}
     * @param capsMode {@link CapsMode}
     * @param renderContext {@link RenderContext} or null to use {@link RenderContextToString} with GlobalData
     */
    public AdjectiveRenderer(Adjective.Form form, ArticleMode articleMode, CapsMode capsMode, RenderContext<Adjective> renderContext) {
        super(
                renderContext == null ? new RenderContextToString<>(GlobalData.getWordData().getAdjectives()) : renderContext,
                articleMode,
                capsMode
        );
        this.form = form;
    }

    @Override
    public Word.Type getType() {
        return Word.Type.Adjective;
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
        private Adjective.Form form = Adjective.Form.positive;
        private ArticleMode articleMode = ArticleMode.none;
        private CapsMode capsMode = CapsMode.none;
        private RenderContext<Adjective> renderContext;
        private String loadKey;
        private String saveKey;
        private String rhymeKey;
        private String rhymeWith;
        private int syllablesDesired;
        private String fallback;
        private Predicate<BaseWordRenderer<Adjective>> fallbackPredicate;

        public Builder(SentenceRendererBuilder sentenceBuilder) {
            this.sentenceBuilder = sentenceBuilder;
        }

        /**
         * Add to provided SentenceRendererBuilder
         * @return {@link SentenceRendererBuilder} provided in constructor
         */
        public SentenceRendererBuilder build() {
            AdjectiveRenderer renderer = new AdjectiveRenderer(form, articleMode, capsMode, renderContext);
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

        public Builder withForm(Adjective.Form form) {
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

        public Builder withRenderContext(RenderContext<Adjective> renderContext) {
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

        public Builder withFallback(String fallback, Predicate<BaseWordRenderer<Adjective>> fallbackPredicate) {
            this.fallback = fallback;
            this.fallbackPredicate = fallbackPredicate;
            return this;
        }
    }
    
    @Override
    public String getFormName() {
        return form.name();
    }

    @Override
    public void setForm(String formName) {
        if (StringUtils.isNotEmpty(formName)) {
            try {
                this.form = Adjective.Form.valueOf(StringUtils.trim(formName).toLowerCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid form name for this={} formName={}", this, formName);
            }
        }
    }

    public Adjective.Form getForm() {
        return form;
    }

    public void setForm(Adjective.Form form) {
        this.form = form;
    }

    @Override
    protected String selectWord(Word word) {
        switch (form) {
            // Comparative
            case comparative:
                return ((Adjective)word).getComparative();

            // Superlative
            case superlative:
                return ((Adjective)word).getSuperlative();

            default:
                return super.selectWord(word);
        }
    }
}
