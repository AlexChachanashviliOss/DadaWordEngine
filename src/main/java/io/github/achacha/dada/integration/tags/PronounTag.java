package io.github.achacha.dada.integration.tags;

import io.github.achacha.dada.engine.data.Pronoun;
import io.github.achacha.dada.engine.render.PronounRenderer;

public class PronounTag extends BaseWordTag<Pronoun, PronounRenderer> {
    public PronounTag() {
        super(new PronounRenderer(new RenderContextToJspTag<>(GlobalData.getWordData().getPronouns())));
    }
}
