package io.github.achacha.dada.engine.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordHelperTest {
    @Test
    public void testIsVowel() {
        assertTrue(WordHelper.isVowel('a'));
        assertTrue(WordHelper.isVowel('e'));
        assertTrue(WordHelper.isVowel('i'));
        assertTrue(WordHelper.isVowel('o'));
        assertTrue(WordHelper.isVowel('u'));
        assertFalse(WordHelper.isVowel('h'));
        assertFalse(WordHelper.isVowel('b'));

        assertTrue(WordHelper.isVowelOrH('a'));
        assertTrue(WordHelper.isVowelOrH('e'));
        assertTrue(WordHelper.isVowelOrH('i'));
        assertTrue(WordHelper.isVowelOrH('o'));
        assertTrue(WordHelper.isVowelOrH('u'));
        assertTrue(WordHelper.isVowelOrH('h'));
        assertFalse(WordHelper.isVowelOrH('b'));
    }

    @Test
    public void testIsConsonant() {
        assertFalse(WordHelper.isConsonant('y'));
        assertTrue(WordHelper.isConsonant('h'));
        assertTrue(WordHelper.isConsonant('b'));
        assertTrue(WordHelper.isConsonant('z'));

        assertTrue(WordHelper.isConsonantOrY('y'));
        assertTrue(WordHelper.isConsonantOrY('h'));
        assertTrue(WordHelper.isConsonantOrY('f'));
        assertTrue(WordHelper.isConsonantOrY('w'));
    }

    @Test
    public void testHelpers() {
        assertEquals('\u0000', WordHelper.getLast(""));
        assertEquals('a', WordHelper.getLast("a"));
        assertEquals('a', WordHelper.getLast(" a"));

        assertEquals('\u0000', WordHelper.getOneBeforeLast(""));
        assertEquals('\u0000', WordHelper.getOneBeforeLast(" "));
        assertEquals('a', WordHelper.getOneBeforeLast("a "));
        assertEquals('a', WordHelper.getOneBeforeLast(" a "));
    }

    @Test
    public void testPlural() {
        assertEquals("dogs", WordHelper.makePlural("dog"));
        assertEquals("taxes", WordHelper.makePlural("tax"));

        assertEquals("wolves", WordHelper.makePlural("wolf"));

        assertEquals("scarves", WordHelper.makePlural("scarf"));
        assertEquals("knives", WordHelper.makePlural("knife"));
        assertEquals("bikes", WordHelper.makePlural("bike"));

        assertEquals("witches", WordHelper.makePlural("witch"));
        assertEquals("wishes", WordHelper.makePlural("wish"));
        assertEquals("baths", WordHelper.makePlural("bath"));

        assertEquals("plays", WordHelper.makePlural("play"));
        assertEquals("plies", WordHelper.makePlural("ply"));

        assertEquals("zoos", WordHelper.makePlural("zoo"));
        assertEquals("bos", WordHelper.makePlural("bo"));
    }
}
