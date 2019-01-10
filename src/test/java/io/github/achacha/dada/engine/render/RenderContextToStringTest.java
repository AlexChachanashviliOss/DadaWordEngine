package io.github.achacha.dada.engine.render;

import io.github.achacha.dada.engine.data.Text;
import io.github.achacha.dada.engine.data.WordsByType;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RenderContextToStringTest {

    @Test
    public void testAttribute() {
        RenderContextToString<Text> context = new RenderContextToString<>(WordsByType.empty());
        assertNull(context.getAttribute("form"));

        context.setAttribute("form", "plural");
        assertEquals("plural", context.getAttribute("form"));

        context.setAttribute("form", null);
        assertNull(context.getAttribute("form"));
    }

    @Test
    public void getWriter() throws IOException {
        RenderContextToString<Text> context = new RenderContextToString<>(WordsByType.empty());
        context.getWriter().write("ThisIsATest");
        assertEquals("ThisIsATest", context.getWriter().toString());
    }
}