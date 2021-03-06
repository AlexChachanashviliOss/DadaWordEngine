package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Preposition;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;

import java.util.function.Predicate;

public class PrepositionRenderer extends BaseWordRenderer<Preposition> {
    public PrepositionRenderer() {
        super(new RenderContextToString<>(GlobalData.getWordData().getPrepositions()));
    }

    /**
     * Custom renderer context
     * @param renderContext {@link RenderContext}
     */
    public PrepositionRenderer(RenderContext<Preposition> renderContext) {
        super(renderContext);
    }

    /**
     * Extended constructor
     * @param articleMode {@link ArticleMode}
     * @param capsMode {@link CapsMode}
     * @param renderContext {@link RenderContext} or null to use {@link RenderContextToString} with GlobalData
     */
    public PrepositionRenderer(ArticleMode articleMode, CapsMode capsMode, RenderContext<Preposition> renderContext) {
        super(
                renderContext == null ? new RenderContextToString<>(GlobalData.getWordData().getPrepositions()) : renderContext,
                articleMode,
                capsMode
        );
    }

    @Override
    public Word.Type getType() {
        return Word.Type.Preposition;
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
        private ArticleMode articleMode = ArticleMode.none;
        private CapsMode capsMode = CapsMode.none;
        private RenderContext<Preposition> renderContext;
        private String loadKey;
        private String saveKey;
        private String rhymeKey;
        private String rhymeWith;
        private int syllablesDesired;
        private String fallback;
        private Predicate<BaseWordRenderer<Preposition>> fallbackPredicate;

        public Builder(SentenceRendererBuilder sentenceBuilder) {
            this.sentenceBuilder = sentenceBuilder;
        }

        /**
         * Add to provided SentenceRendererBuilder
         * @return {@link SentenceRendererBuilder} provided in constructor
         */
        public SentenceRendererBuilder build() {
            PrepositionRenderer renderer = new PrepositionRenderer(articleMode, capsMode, renderContext);
            renderer.loadKey = loadKey;
            renderer.saveKey = saveKey;
            renderer.rhymeKey = rhymeKey;
            renderer.rhymeWith = rhymeWith;
            renderer.syllablesDesired = syllablesDesired;
            renderer.fallback = fallback;
            renderer.fallbackPredicate = fallbackPredicate;

            sentenceBuilder.getRenderers().add(renderer);

            return sentenceBuilder;
        }

        public Builder withArticleMode(ArticleMode articleMode) {
            this.articleMode = articleMode;
            return this;
        }

        public Builder withCapsMode(CapsMode capsMode) {
            this.capsMode = capsMode;
            return this;
        }

        public Builder withRenderContext(RenderContext<Preposition> renderContext) {
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

        public Builder withFallback(String fallback, Predicate<BaseWordRenderer<Preposition>> fallbackPredicate) {
            this.fallback = fallback;
            this.fallbackPredicate = fallbackPredicate;
            return this;
        }

    }

    @Override
    public String getFormName() {
        return Preposition.Form.none.name();
    }

    @Override
    public void setForm(String formName) {
        // NO-OP, word has no forms
    }
}
