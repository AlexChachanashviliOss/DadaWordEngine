package io.github.achacha.dada.integration.tags;

import io.github.achacha.dada.engine.data.Verb;
import io.github.achacha.dada.engine.render.VerbRenderer;

public class VerbTag extends BaseWordTag<Verb, VerbRenderer> {
    public VerbTag() {
        super(new VerbRenderer(new RenderContextToJspTag<>(GlobalData.getWordData().getVerbs())));
    }
}
