package io.github.achacha.dada.engine.phonemix;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.achacha.dada.engine.phonemix.PhonemixTransformerBase.NONE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhonemixTransformerBaseTest {
    @Test
    public void verifyInternals() {
        assertEquals('_', NONE);   // Make sure this is not changed
    }

    @Test
    public void testTransformAndIndex() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();

        String input = "Hello World, this is a test!";
        List<Pair<String,Integer>> list = transformer.transformAndIndex(input);

        String transformedWords = list.stream().map(Pair::getLeft).collect(Collectors.joining(","));
        List<Integer> offsets = list.stream().map(Pair::getRight).collect(Collectors.toList());

        assertEquals("hl,wrld,zs,is,a,tst", transformedWords);
        assertEquals(Arrays.asList(0, 6, 13, 18, 21, 23), offsets);
        assertEquals('H', input.charAt(offsets.get(0)));
        assertEquals('W', input.charAt(offsets.get(1)));
        assertEquals('t', input.charAt(offsets.get(2)));
        assertEquals('i', input.charAt(offsets.get(3)));
        assertEquals('a', input.charAt(offsets.get(4)));
        assertEquals('t', input.charAt(offsets.get(5)));
    }

    @Test
    public void testReverseTransformAndIndex() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().withReverse().build();

        String input = "Hello World, this is a test!";
        List<Pair<String,Integer>> list = transformer.transformAndIndex(input);

        String transformedWords = list.stream().map(Pair::getLeft).collect(Collectors.joining(","));
        List<Integer> offsets = list.stream().map(Pair::getRight).collect(Collectors.toList());

        assertEquals("lh,dlrw,sz,si,a,tst", transformedWords);
        assertEquals(Arrays.asList(0, 6, 13, 18, 21, 23), offsets);
        assertEquals('H', input.charAt(offsets.get(0)));
        assertEquals('W', input.charAt(offsets.get(1)));
        assertEquals('t', input.charAt(offsets.get(2)));
        assertEquals('i', input.charAt(offsets.get(3)));
        assertEquals('a', input.charAt(offsets.get(4)));
        assertEquals('t', input.charAt(offsets.get(5)));
    }

    @Test
    public void testWithStartPosition() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder()
                .withStartPosition(8)
                .build();

        assertEquals("", transformer.transform("PROMPT>"));
        assertEquals(0, transformer.transformAndIndex("PROMPT>").size());

        assertEquals("hl", transformer.transform("PROMPT> Hello"));

        List<Pair<String,Integer>> indexed = transformer.transformAndIndex("PROMPT> Hello");
        assertEquals(1, indexed.size());
        assertEquals("hl", indexed.get(0).getLeft());
        assertEquals(8, indexed.get(0).getRight().intValue());
    }

    @Test
    public void testWithReverse() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder()
                .withReverse()
                .build();

        assertEquals("lh dlrw sz si a tst", transformer.transform("Hello World, this is a test!"));
        assertEquals("yzlf", transformer.transform("Filthy"));
        assertEquals("ytw", transformer.transform("witty"));
    }

    @Test
    public void testCompactDuplicates() {
        PhoneticTransformer transformer = PhoneticTransformerBuilder.builder().build();

        assertEquals("sklnt", transformer.transform("succulent"));
    }

    private void assertOne(PhonemixTransformerBase transformer, String expected, String inputString) {
        char[] inputA = inputString.toCharArray();
        transformer.transformOne(inputA);
        assertArrayEquals(expected.toCharArray(), inputA);
    }

    private void assertTwo(PhonemixTransformerBase transformer, String expected, String inputString) {
        char[] inputA = inputString.toCharArray();
        transformer.transformDigraph(inputA);
        assertArrayEquals(expected.toCharArray(), inputA);
    }

    private void assertThree(PhonemixTransformerBase transformer, String expected, String inputString) {
        char[] input = inputString.toCharArray();
        transformer.transformTrigraph(input);
        assertArrayEquals(expected.toCharArray(), input);
    }

    @Test
    public void testCompactingTrigraph() {
        PhonemixTransformerBase transformer = (PhonemixTransformerBase)PhoneticTransformerBuilder.builder().build();

        // c
        assertThree(transformer,"_Sa", "cia");
        assertThree(transformer,"_So", "cio");
        assertThree(transformer,"cid", "cid");

        // g
        assertThree(transformer,"__t", "ght");

        // s
        assertThree(transformer,"_sk", "sch");
        assertThree(transformer,"ska", "sca");
        assertThree(transformer,"_se", "sce");
        assertThree(transformer,"_si", "sci");
        assertThree(transformer,"sko", "sco");
        assertThree(transformer,"sku", "scu");

        // w
        assertThree(transformer,"_hO", "who");
        assertThree(transformer,"whe", "whe");  // digraphs handle this case
    }

    @Test
    public void testCompactingDigraph() {
        PhonemixTransformerBase transformer = (PhonemixTransformerBase)PhoneticTransformerBuilder.builder().build();

        // a
        assertTwo(transformer,"_O", "au");

        // c
        assertTwo(transformer,"ka", "ca");
        assertTwo(transformer,"ko", "co");
        assertTwo(transformer,"ku", "cu");

        assertTwo(transformer,"_k", "cc");
        assertTwo(transformer,"_k", "ck");

        assertTwo(transformer,"se", "ce");
        assertTwo(transformer,"si", "ci");

        assertTwo(transformer,"_C", "ch");

        // d
        assertTwo(transformer,"_j", "dg");

        // e
        assertTwo(transformer,"_u", "eu");

        // g
        assertTwo(transformer,"_f", "gh");    // End of word
        assertTwo(transformer,"_go", "gho");  // Not end of word
        assertTwo(transformer,"_n", "gn");

        // k
        assertTwo(transformer,"_n", "kn");

        // o
        assertTwo(transformer,"_O", "oo");
        assertTwo(transformer,"_O", "ou");

        // p
        assertTwo(transformer,"_f", "ph");
        assertTwo(transformer,"_s", "ps");

        // t
        assertTwo(transformer,"_z", "th");
        assertTwo(transformer,"o_S", "oti");  // sh sound when following vowel
        assertTwo(transformer,"cti", "cti");  // ti not changed

        // s
        assertTwo(transformer,"_s", "sc");    // trigraph handles sc[aeiouh]
        assertTwo(transformer,"_S", "sh");

        // w
        assertTwo(transformer,"_w", "wh");
        assertTwo(transformer,"_r", "wr");

        // z
        assertTwo(transformer,"tz", "zz");
        assertTwo(transformer,"_z", "zh");
    }

    @Test
    public void testCompactingOne() {
        PhonemixTransformerBase transformer = (PhonemixTransformerBase)PhoneticTransformerBuilder.builder().build();

        // Vowel
        assertOne(transformer, "a", "a");   // Leading vowel kept
        assertOne(transformer, "b_", "ba");
        assertOne(transformer, "b_", "be");
        assertOne(transformer, "b_", "bi");
        assertOne(transformer, "b_", "bo");
        assertOne(transformer, "b_", "bu");
        assertOne(transformer, "b_", "bh");

        assertOne(transformer, "k", "q");
    }

    @Test
    public void testAccess() {
        PhonemixTransformerBase transformer = (PhonemixTransformerBase) PhoneticTransformerBuilder.builder().build();

        assertEquals(NONE, transformer.getPrevious("".toCharArray(), 0));
        assertEquals(NONE, transformer.getPrevious("ab".toCharArray(), 0));
        assertEquals('a', transformer.getPrevious("ab".toCharArray(), 1));

        assertEquals(NONE, transformer.getNext("".toCharArray(), 0));
        assertEquals('b', transformer.getNext("ab".toCharArray(), 0));
        assertEquals(NONE, transformer.getNext("ab".toCharArray(), 1));
    }

    @Test
    public void testNexts() {
        PhonemixTransformerBase transformer = (PhonemixTransformerBase) PhoneticTransformerBuilder.builder().build();

        assertEquals(NONE, transformer.getNext("".toCharArray(), 0));
        assertEquals(NONE, transformer.getNext("___".toCharArray(), 0));
        assertEquals(NONE, transformer.getNext("".toCharArray(), 1));
        assertEquals(NONE, transformer.getNext("a".toCharArray(), 0));
        assertEquals('a', transformer.getNext("__a".toCharArray(), 0));
        assertEquals('b', transformer.getNext("ab_".toCharArray(), 0));

        assertEquals(NONE, transformer.getNextNext("".toCharArray(), 0));
        assertEquals(NONE, transformer.getNextNext("___".toCharArray(), 0));
        assertEquals(NONE, transformer.getNextNext("_a__".toCharArray(), 0));
        assertEquals(NONE, transformer.getNextNext("a_b__".toCharArray(), 0));
        assertEquals('c', transformer.getNextNext("ab__c".toCharArray(), 0));
        assertEquals('c', transformer.getNextNext("ab__cd_".toCharArray(), 0));
    }
}
