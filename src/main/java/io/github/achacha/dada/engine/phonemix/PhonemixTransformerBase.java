package io.github.achacha.dada.engine.phonemix;

import com.google.common.annotations.VisibleForTesting;
import io.github.achacha.dada.engine.base.WordHelper;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Given a string will transform into phonemix format
 * <p>
 * Phonemix is a made-up word to represent form of a word converted and compacted into it's phoneme-like
 * representation that can be used in forward and reverse
 * <p>
 * <p>
 * Original compacting algorithm is very loosely based on ideas in Russell Soundex (1918)
 * Compacting algorithm designed in 1993 by Alex Chachanashvili for Dada Wallpaper Poem BBS art project (updated in 2004)
 * Aggressive algorithm designed in 2017 by Alex Chachanashvili for Dada poem generator and Discord bot, wider search results
 * Enhanced algorithm designed in 2021 by Alex Chachanashvili for better rhyming and accurate search
 * <p>
 * <p>
 * Algorithms are designed to be used forwards and backwards to allow sound based matching and rhyme detection
 * {@link PhonemixCompactingTransformer} - Some consonant pairs follow custom remapping based on American English
 * {@link PhonemixAggressiveTransformer} - Aggressive compactor removes and compacts based on American English
 * {@link PhonemixEnhancedTransformer} - Enhanced extends vowel pronunciation based on proximity of other letters
 */
public abstract class PhonemixTransformerBase implements PhoneticTransformer {
    protected static final Logger LOGGER = LogManager.getLogger(PhonemixTransformerBase.class);

    // Letter replaced when it has no impact on sound
    static final char NONE = '_';

    /**
     * Start position inside the text passed into transform
     */
    protected final int startPos;

    /**
     * If to keep the leading vowel when processing, vowels may be dropped in some transformers
     * When withReverse is enabled, this acts as a trailing vowel
     */
    protected final boolean keepLeadingVowel;

    /**
     * If reverse all data before transform
     */
    protected final boolean reverse;

    protected PhonemixTransformerBase(PhoneticTransformerBuilder builder) {
        this.startPos = builder.startPos;
        this.keepLeadingVowel = builder.keepLeadingVowel;
        this.reverse = builder.reverse;
    }

    @Nonnull
    @Override
    public String transform(String text) {
        if (text.length() >= startPos) {
            return Arrays.stream(text.substring(startPos).toLowerCase().split(" "))
                    .map(word -> transformWord(word.toCharArray()))
                    .map(this::processAfterXform)
                    .collect(Collectors.joining(" "));
        }

        return "";
    }

    @Nonnull
    @Override
    public List<Pair<String, Integer>> transformAndIndex(String text) {
        if (text.length() >= startPos) {
            ArrayList<Pair<String, Integer>> list = new ArrayList<>();
            Pattern p = Pattern.compile("\\S+");
            Matcher m = p.matcher(text.substring(startPos).toLowerCase());
            while (m.find()) {
                String word = m.group();
                String transformed = transformWord(word.toCharArray());
                list.add(Pair.of(processAfterXform(transformed), m.start() + startPos));
            }
            return list;
        } else
            return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "PhonemixTransformerBase{" +
                "type=" + getClass().getSimpleName() +
                ", startPos=" + startPos +
                ", keepLeadingVowel=" + keepLeadingVowel +
                ", reverse=" + reverse +
                '}';
    }

    /**
     * Apply any rules after xform of each text block
     *
     * @param text String
     * @return String
     */
    @Nonnull
    protected String processAfterXform(@Nonnull String text) {
        if (reverse)
            return StringUtils.reverse(text);
        else
            return text;
    }

    /**
     * Transform a word (does not split sentences)
     *
     * @param word char[]
     * @return Resulting phonemix word
     */
    protected String transformWord(char[] word) {
        LOGGER.debug("=|word={}", word);
        preprocess(word);
        int leftInWord = word.length;
        if (leftInWord > 2)
            transformTrigraph(word);
        if (leftInWord > 1)
            transformDigraph(word);
        if (leftInWord > 0)
            transformOne(word);
        return postprocess(word);
    }

    /**
     * Transform trigraphs
     */
    @VisibleForTesting
    void transformTrigraph(char[] s) {
        int i = 0;
        int endPos = s.length - 2;
        while (i < endPos) {
            switch (s[i]) {
                // ght -> __t
                case 'g':
                    if (s[i + 1] == 'h' && s[i + 2] == 't') {
                        LOGGER.debug("> ght detected, i={} s={}", i, s);
                        s[i] = NONE;
                        s[i + 1] = NONE;
                        s[i + 2] = 't';
                        i += 2;
                        LOGGER.debug("< ght->__t, i={} s={}", i, s);
                    }
                    break;

                // c
                case 'c':
                    // ci followed by vowel pronounced as sh and vowel
                    if (s[i + 1] == 'i' && WordHelper.isVowel(s[i + 2])) {
                        LOGGER.debug("> ci[aeiou] detected, i={} s={}", i, s);
                        s[i] = NONE;
                        s[i + 1] = 'S';
                        // +2 unchanged
                        i += 2;
                        LOGGER.debug("< ci[aeiou]->_S[aeiou], i={} s={}", i, s);
                    }
                    break;

                // s
                case 's':
                    switch(s[i + 1]) {
                        // sc
                        case 'c':
                            switch(s[i + 2]) {
                                // sch -> _sk
                                case 'h':
                                    LOGGER.debug("> sch detected, i={} s={}", i, s);
                                    s[i] = NONE;
                                    s[i + 1] = 's';
                                    s[i + 2] = 'k';
                                    i += 2;
                                    LOGGER.debug("< sch->_sk, i={} s={}", i, s);
                                    break;

                                // sci, sce -> __S   (sh sound with [ei] trailing)
                                case 'e':
                                case 'i':
                                    LOGGER.debug("> sc[ei] detected, i={} s={}", i, s);
                                    s[i] = NONE;
                                    s[i + 1] = 's';
                                    // +2 unchanged
                                    i += 2;
                                    LOGGER.debug("< sc[ei]->_s[ei], i={} s={}", i, s);
                                    break;

                                // sca, sco -> __S   (sk sound with [aou] trailing)
                                case 'a':
                                case 'o':
                                case 'u':
                                    LOGGER.debug("> sc[aou] detected, i={} s={}", i, s);
                                    s[i] = 's';
                                    s[i + 1] = 'k';
                                    // +2 unchanged
                                    i += 2;
                                    LOGGER.debug("< sc[aou]->sk[aou], i={} s={}", i, s);
                                    break;
                            }
                            break;
                    }
                    break;

                // who -> hO  (wh treated as w in digraph and o sound extended as oo)
                case 'w':
                    if (s[i + 1] == 'h' && s[i + 2] == 'o') {
                        LOGGER.debug("> who detected, i={} s={}", i, s);
                        s[i] = NONE;
                        s[i + 1] = 'h';
                        s[i + 2] = 'O';
                        i += 2;
                        LOGGER.debug("< who->_hO, i={} s={}", i, s);
                    }
                    break;
            }
            ++i;
        }
    }

    /**
     * Transform digraphs
     */
    @VisibleForTesting
    void transformDigraph(char[] s) {
        int i = 0;
        int endPos = s.length - 1;
        char last = ' ';            // Letter before the digraph
        while (i < endPos) {
            switch (s[i]) {
                case 'a':
                    switch (s[i + 1]) {
                        // au -> O
                        case 'u':
                            LOGGER.debug(" > au detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'O';
                            ++i;
                            LOGGER.debug(" < au->_O, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'c':
                    switch (s[i + 1]) {
                        // c that sounds like k
                        case 'a':
                        case 'o':
                        case 'u':
                            LOGGER.debug(" > c sounds like k, i={} s={}", i, s);
                            s[i] = 'k';
                            LOGGER.debug(" < c->k, i={} s={}", i, s);
                            break;

                        // c? sounds like k
                        case 'c':
                        case 'k':
                            LOGGER.debug(" > c? sounds like k, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i+1] = 'k';
                            LOGGER.debug(" < c?->_k, i={} s={}", i, s);
                            ++i;
                            break;


                        // c that sounds like s
                        case 'e':
                        case 'i':
                            LOGGER.debug(" > c sounds like s, i={} s={}", i, s);
                            s[i] = 's';
                            LOGGER.debug(" < c->s, i={} s={}", i, s);
                            break;

                        // ch -> C
                        case 'h':
                            LOGGER.debug(" > ch detected, i={} s={}", i, s);
                            s[i] = '_';
                            s[i+1] = 'C';
                            ++i;
                            LOGGER.debug(" < ch->_C, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'd':
                    // dg -> j
                    switch (s[i + 1]) {
                        case 'g':
                            LOGGER.debug(" > dg detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'j';
                            ++i;
                            LOGGER.debug(" < dg->_j, i={} s={}", i, s);
                            break;
                    }
                    break;


                case 'e':
                    // eu -> u
                    switch (s[i + 1]) {
                        case 'u':
                            LOGGER.debug(" > eu detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'u';
                            ++i;
                            LOGGER.debug(" < eu->_u, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'g':
                    switch (s[i + 1]) {
                        // gh -> g, gh -> f at end of word
                        case 'h':
                            if (i + 1 == endPos) {
                                LOGGER.debug(" > gh detected at end, i={} s={}", i, s);
                                s[i] = NONE;
                                s[i + 1] = 'f';
                                ++i;
                                LOGGER.debug(" < gh->_f, i={} s={}", i, s);
                                // End of the word
                            }
                            else {
                                // Not end of word
                                LOGGER.debug(" > gh detected not at end, i={} s={}", i, s);
                                s[i] = NONE;
                                s[i + 1] = 'g';
                                ++i;
                                LOGGER.debug(" < gh->_g, i={} s={}", i, s);
                            }
                            break;

                        // gn -> n
                        case 'n':
                            LOGGER.debug(" > gn sounds like n, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'n';
                            ++i;
                            LOGGER.debug(" < gn->_n, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'k':
                    switch (s[i + 1]) {
                        // kn -> n
                        case 'n':
                            LOGGER.debug(" > kn sounds like n, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'n';
                            ++i;
                            LOGGER.debug(" < kn->_n, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'o':
                    switch (s[i + 1]) {
                        // oo -> O
                        case 'o':
                            LOGGER.debug(" > oo detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'O';
                            ++i;
                            LOGGER.debug(" < oo->_O, i={} s={}", i, s);
                            break;

                        // ou -> O
                        case 'u':
                            LOGGER.debug(" > ou detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'O';
                            ++i;
                            LOGGER.debug(" < ou->_O, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'p':
                    switch (s[i + 1]) {
                        // ph -> f
                        case 'h':
                            LOGGER.debug(" > ph detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'f';
                            ++i;
                            LOGGER.debug(" < ph->_f, i={} s={}", i, s);
                            break;

                        // ps -> s
                        case 's':
                            LOGGER.debug(" > ps detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 's';
                            ++i;
                            LOGGER.debug(" < ps->_s, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 's':
                    switch (s[i + 1]) {
                        // sh -> S
                        case 'h':
                            LOGGER.debug(" > sh detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i+1] = 'S';
                            LOGGER.debug(" < sh->_S, i={} s={}", i, s);
                            ++i;
                            break;

                        // sc -> s   (trigraph handles sce- sci- sca- sco- scu-)
                        case 'c':
                            LOGGER.debug(" > sc detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i+1] = 's';
                            LOGGER.debug(" < sc->_s, i={} s={}", i, s);
                            ++i;
                            break;
                    }
                    break;

                case 't':
                    switch (s[i + 1]) {
                        // th -> z
                        case 'h':
                            LOGGER.debug(" > th detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'z';
                            ++i;
                            LOGGER.debug(" < th->_z, i={} s={}", i, s);
                            break;

                        // ti -> S is preceeded by vowel
                        case 'i':
                            if (WordHelper.isVowelOrH(last)) {
                                LOGGER.debug(" > th detected after vowel, i={} s={}", i, s);
                                s[i] = NONE;
                                s[i + 1] = 'S';
                                ++i;
                                LOGGER.debug(" < th->_S, i={} s={}", i, s);
                            }
                            break;
                    }
                    break;

                case 'w':
                    switch (s[i + 1]) {
                        // wh -> w
                        case 'h':
                            LOGGER.debug(" > wh detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'w';
                            ++i;
                            LOGGER.debug(" < wh->_w, i={} s={}", i, s);
                            break;

                        // wr -> r
                        case 'r':
                            LOGGER.debug(" > wr detected, i={} s={}", i, s);
                            s[i] = NONE;
                            s[i + 1] = 'r';
                            ++i;
                            LOGGER.debug(" < wr->_r, i={} s={}", i, s);
                            break;
                    }
                    break;

                case 'z':
                    // zz -> tz
                    switch (s[i + 1]) {
                        case 'z':
                            LOGGER.debug(" > zz detected, i={} s={}", i, s);
                            s[i] = 't';
                            ++i;
                            LOGGER.debug(" < zz->tz, at i={} s={}", i, s);
                            break;

                        // zh -> Z sounds like z
                        case 'h':
                            LOGGER.debug(" > zh sounds like z, i={} s={}", i, s);
                            s[i] = '_';
                            s[i+1] = 'z';
                            LOGGER.debug(" < zh->_z, i={} s={}", i, s);
                            break;
                    }
                    break;

                default:
                    if (s[i] == s[i + 1]) {
                        LOGGER.debug(" > Duplicate detected, i={} s={}", i, s);
                        // Compact duplicate letters but removing the first of the two and continuing
                        s[i] = NONE;
                        ++i;
                        LOGGER.debug(" < Duplicate compacted, i={} s={}", i, s);
                    }
            }
            last = s[i++];
        }
    }

    @VisibleForTesting
    void transformOne(char[] s) {
        for (int i = 0; i < s.length; ++i) {
            // Remove vowels and h, does not remove compound vowels
            switch (s[i]) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                case 'h':
                    if (i == 0 && keepLeadingVowel) {
                        LOGGER.debug(" | Keeping leading vowel, i={} s={}", i, s);
                    } else {
                        LOGGER.debug(" < vowel removal, i={} s={}", i, s);
                        s[i] = NONE;
                        LOGGER.debug(" > ?->_, i={} s={}", i, s);
                    }
                    break;

                case 'q':
                    LOGGER.debug(" < q detected, i={} s={}", i, s);
                    s[i] = 'k';
                    LOGGER.debug(" > q->k, i={} s={}", i, s);
                    break;
            }
        }
    }

    /**
     * Pre-processing
     * @param s string
     */
    protected void preprocess(char[] s) {
    }

    /**
     * Post-processing and compacting
     *
     * @param sentence char[]
     * @return String
     */
    protected String postprocess(char[] sentence) {
        StringBuilder str = new StringBuilder(sentence.length);

        // Compact by removing duplicates and NONE
        char lastChar = 0;
        for (char value : sentence)
            if (value != NONE && value != lastChar && CharUtils.isAsciiAlphanumeric(value)) {
                str.append(value);
                lastChar = value;
            }

        return str.toString();
    }

    /**
     * Get previous character, skipping over NONE
     *
     * @param s string
     * @param i current position
     * @return previous character or NONE if nothing before it
     */
    @VisibleForTesting
    char getPrevious(char[] s, int i) {
        while (--i >= 0) {
            if (s[i] != NONE) {
                return s[i];
            }
        }
        return NONE;
    }

    /**
     * Get next character, skipping over NONE
     *
     * @param s string
     * @param i current position
     * @return next character or NONE if nothing after it
     */
    char getNext(char[] s, int i) {
        while (++i < s.length) {
            if (s[i] != NONE) {
                return s[i];
            }
        }
        return NONE;
    }

    /**
     * Get next after next character, skipping over NONE
     *
     * @param s string
     * @param i current position
     * @return next after next character or NONE if nothing after it
     */
    char getNextNext(char[] s, int i) {
        boolean haveNext = false;
        while (++i < s.length) {
            if (s[i] != NONE) {
                if (haveNext)
                    return s[i];
                else
                    haveNext = true;
            }
        }
        return NONE;
    }

    /**
     * Check if character is a vowel
     * Overridden for some transformers where vowels are extended beyond the usual suspects
     *
     * @param c character
     * @return true if considered a vowel
     */
    protected boolean isVowel(char c) {
        return WordHelper.isVowel(c);
    }
}
