package io.github.achacha.dada.integration.tags;

import io.github.achacha.dada.engine.data.Adjective;
import io.github.achacha.dada.engine.render.AdjectiveRenderer;

public class AdjectiveTag extends BaseWordTag<Adjective> {
    public AdjectiveTag() {
        super(new AdjectiveRenderer(new RenderContextToJspTag<>(TagSingleton.getWordData().getAdjectives())));
    }
}
