package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.builder.SentenceRendererBuilder;
import io.github.achacha.dada.engine.data.Verb;
import io.github.achacha.dada.engine.data.Word;
import io.github.achacha.dada.integration.tags.GlobalData;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class VerbRenderer extends BaseWordRenderer<Verb> {
    protected Verb.Form form = Verb.Form.base;

    public VerbRenderer() {
        super(new RenderContextToString<>(GlobalData.getWordData().getVerbs()));
    }

    public VerbRenderer(RenderContext<Verb> renderContext) {
        super(renderContext);
    }

    /**
     * Extended constructor
     * @param form {@link Verb.Form}
     * @param articleMode {@link ArticleMode}
     * @param capsMode {@link CapsMode}
     * @param renderContext {@link RenderContext} or null to use {@link RenderContextToString} with GlobalData
     */
    public VerbRenderer(Verb.Form form, ArticleMode articleMode, CapsMode capsMode, RenderContext<Verb> renderContext) {
        super(
                renderContext == null ? new RenderContextToString<>(GlobalData.getWordData().getVerbs()) : renderContext,
                articleMode,
                capsMode
        );
        this.form = form;
    }

    @Override
    public Word.Type getType() {
        return Word.Type.Verb;
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
        private Verb.Form form = Verb.Form.base;
        private ArticleMode articleMode = ArticleMode.none;
        private CapsMode capsMode = CapsMode.none;
        private RenderContext<Verb> renderContext;
        private String loadKey;
        private String saveKey;
        private String rhymeKey;
        private String rhymeWith;
        private int syllablesDesired;
        private String fallback;
        private Predicate<BaseWordRenderer<Verb>> fallbackPredicate;

        public Builder(SentenceRendererBuilder sentenceBuilder) {
            this.sentenceBuilder = sentenceBuilder;
        }

        /**
         * Add to provided SentenceRendererBuilder
         * @return {@link SentenceRendererBuilder} provided in constructor
         */
        public SentenceRendererBuilder build() {
            VerbRenderer renderer = new VerbRenderer(form, articleMode, capsMode, renderContext);
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

        public Builder withForm(Verb.Form form) {
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

        public Builder withRenderContext(RenderContext<Verb> renderContext) {
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

        public Builder withFallback(String fallback, Predicate<BaseWordRenderer<Verb>> fallbackPredicate) {
            this.fallback = fallback;
            this.fallbackPredicate = fallbackPredicate;
            return this;
        }
    }

    @Override
    protected String selectWord(Word word) {
        switch (form) {
            case infinitive:
                return ((Verb)word).getInfinitive();
            case past:
                return ((Verb)word).getPast();
            case singular:
                return ((Verb)word).getSingular();
            case present:
                return ((Verb)word).getPresent();
            case pastparticiple:
                return ((Verb)word).getPastParticiple();
            default:
                return super.selectWord(word);
        }
    }

    public Verb.Form getForm() {
        return form;
    }

    public void setForm(Verb.Form form) {
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
                this.form = Verb.Form.valueOf(StringUtils.trim(formName).toLowerCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid form name for this={} formName={}", this, formName);
            }
        }
    }
}
