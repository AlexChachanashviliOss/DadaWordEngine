package io.github.achacha.dada.integration.tags;

import io.github.achacha.dada.engine.data.Text;
import io.github.achacha.dada.engine.data.WordsByType;
import io.github.achacha.dada.engine.render.TextRenderer;

import java.io.IOException;

/**
 * Tag that represents constant text string
 */
public class TextTag extends BaseWordTag<Text, TextRenderer> {
    public TextTag() {
        super(new TextRenderer("", new RenderContextToJspTag<>(WordsByType.empty())));
    }

    /**
     * @param text constant string
     */
    public TextTag(String text) {
        super(new TextRenderer(text, new RenderContextToJspTag<>(WordsByType.empty())));
    }

    @Override
    public void doTag() throws IOException {
        String word = getWordRenderer().execute();
        LOGGER.debug("doTag: word={}", word);
        wordRenderer.getRendererContext().getWriter().write(word);
    }
}
