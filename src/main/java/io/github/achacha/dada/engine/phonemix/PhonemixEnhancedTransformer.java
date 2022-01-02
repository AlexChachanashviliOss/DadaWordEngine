package io.github.achacha.dada.engine.phonemix;


import static io.github.achacha.dada.engine.base.WordHelper.isConsonant;

/**
 * More accurate vowel and sound representation
 * Better for rhyming (reverse transform)
 */
public class PhonemixEnhancedTransformer extends PhonemixTransformerBase {

    PhonemixEnhancedTransformer(PhoneticTransformerBuilder builder) {
        super(builder);
    }

    /**
     * All extended vowels
     */
    private static final String ALL_VOWELS = "aàáâäæãåāeèéêëēėęiîïíīįìoôöòóœøōõuûüùúū";

    /**
     * Pre-process vowels
     *
     * @param s word
     */
    @Override
    protected void preprocess(char[] s) {
        // Single letter words not pre-processed
        if (s.length < 2) return;

        // Compact consonant sounds
        preprocessCompounds(s);

        // Process using a moving window
        for (int i = 0; i < s.length; ++i) {
            // Skip over
            if (s[i] == NONE) continue;

            // Process window
            i += processWindow(s, i);
        }
    }

    protected void preprocessCompounds(char[] s) {
        for (int i = 0; i < s.length; ++i) {
            if (match(s, i, 't', 'h')) {
                // th to ż
                s[i++] = 'ż';
                s[i] = NONE;
                LOGGER.debug(" > th->ż, i={} s={}", i, s);
            } else if (match(s, i, 's', 's')) {
                // ss to ß
                s[i++] = 'ß';
                s[i] = NONE;
                LOGGER.debug(" > ss->ß, i={} s={}", i, s);
            } else if (match(s, i, 's', 'h')) {
                // sh to ś
                s[i++] = 'ś';
                s[i] = NONE;
                LOGGER.debug(" > sh->ś, i={} s={}", i, s);
            } else if (match(s, i, 's', 'c', 'h')) {
                // sch to š
                s[i++] = 'š';
                s[i++] = NONE;
                s[i] = NONE;
                LOGGER.debug(" > sch->š, i={} s={}", i, s);
            } else if (match(s, i, 't', 'c', 'h')) {
                // tch to ć
                s[i++] = 'ć';
                s[i++] = NONE;
                s[i] = NONE;
                LOGGER.debug(" > ch->ć, i={} s={}", i, s);
            } else if (match(s, i, 'c', 'h')) {
                // ch to ć
                s[i++] = 'ć';
                s[i] = NONE;
                LOGGER.debug(" > ch->ć, i={} s={}", i, s);
            } else if (match(s, i, 't', 'z')) {
                // tz to ż
                s[i++] = 'ż';
                s[i] = NONE;
                LOGGER.debug(" > tz->ż, i={} s={}", i, s);
            } else if (match(s, i, 'z', 'z')) {
                // zz to č
                s[i++] = 'č';
                s[i] = NONE;
                LOGGER.debug(" > zz->č, i={} s={}", i, s);
            } else if (match(s, i, 'c', 'k')) {
                // cc to k
                s[i++] = 'k';
                s[i] = NONE;
                LOGGER.debug(" > ck->k, i={} s={}", i, s);
            } else if (match(s, i, 'c', 'c')) {
                // cc to k
                s[i++] = 'k';
                s[i] = NONE;
                LOGGER.debug(" > cc->k, i={} s={}", i, s);
            } else if (match(s, i, 'q', 'u')) {
                // cc to k
                s[i++] = 'k';
                s[i] = 'w';
                LOGGER.debug(" > qu->kw, i={} s={}", i, s);
            }
        }
    }

    void transformOne(char[] s) {
        for (int i = 0; i < s.length; ++i) {
            switch (s[i]) {
                case 'q':
                    // q by itself becomes k (qu handled in transformTwo)
                    s[i] = 'k';
                    LOGGER.debug(" > q->k, i={} s={}", i, s);
                    break;
            }
        }
    }

    /**
     * Post-processing and compacting
     *
     * @param sentence char[]
     * @return String
     */
    protected String postprocess(char[] sentence) {
        StringBuilder str = new StringBuilder(sentence.length);

        // Compact by removing NONE
        for (char value : sentence)
            if (value != NONE) {
                str.append(value);
            }

        return str.toString();
    }

    /**
     * Process a moving window
     * NONE for anything that is not yet there
     *
     * @param s string
     * @param i position
     * @return characters to skip ahead
     */
    private int processWindow(char[] s, int i) {
        switch (s[i]) {
            case 'a': {
                if (match(s, i, 'a', 'y', 'e')) {
                    s[i] = 'á';         // e after ay causes ai
                    s[i + 1] = NONE;
                    s[i + 2] = NONE;
                    LOGGER.debug(" > aye->á, i={} s={}", i, s);
                    return 2;
                }
                if (match(s, i, 'a', 'y')) {
                    s[i] = 'á';         // e after ay causes ai
                    s[i + 1] = NONE;
                    LOGGER.debug(" > ay->á, i={} s={}", i, s);
                    return 1;
                }
                if (match(s, i, 'a', 'l', 'l')) {
                    // Double l causes short o sound single l
                    s[i] = 'o';
                    s[i + 1] = 'l';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > all->ol, i={} s={}", i, s);
                    return 2;
                }

                if (isConsonant(getNext(s, i)) && getNextNext(s, i) == 'e') {
                    // a{consonant}e -> á{consonant} and drop e
                    s[i] = 'á';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > a{consonant}e->a{consonant}, i={} s={}", i, s);
                    return 2;
                }

                char next = getNext(s, i);
                if (next == 'r' || next == 'b' || next == 't' || next == 'f') {
                    s[i] = 'à';         // ah sound
                    LOGGER.debug(" > a->à (ah), i={} s={}", i, s);
                    return 0;
                }
            }
            break;

            case 'c': {
                char next = getNext(s, i);
                if (next == 'e' || next == 'i' || next == 'y') {
                    // c sounds like s
                    s[i] = 's';
                    LOGGER.debug(" < c->k, i={} s={}", i, s);
                    return 0;
                }
                if (next == 'a' || next == 'o' || next == 'u' || isConsonant(next)) {
                    // c sounds like k
                    s[i] = 'k';
                    LOGGER.debug(" < c->k, i={} s={}", i, s);
                    return 0;
                }
            }
            break;

            case 'e': {
                if (
                        match(s, i, 'e', 'e')
                                || match(s, i, 'e', 'a')
                ) {
                    s[i] = 'ë';
                    s[i + 1] = NONE;
                    LOGGER.debug(" > e[ea]->ë, i={} s={}", i, s);
                    return 1;
                }

                char next = getNext(s, i);
                if (next == 'w') {
                    // ew -> ū
                    s[i] = 'ū';
                    s[i + 1] = NONE;
                    LOGGER.debug(" > ew->ū, i={} s={}", i, s);
                    return 1;
                }

                char next2 = getNextNext(s, i);
                if (isConsonant(next) && next2 == 'e') {
                    // a{consonant}e -> á{consonant} and drop e
                    s[i] = 'é';
                    s[i + 2] = 'é';
                    LOGGER.debug(" > e{consonant}e->é{consonant}é, i={} s={}", i, s);
                    return 2;
                }
            }
            break;

            case 'i': {
                char next = getNext(s, i);
                char next2 = getNextNext(s, i);
                if (next == 'g' && next2 == 'h') {
                    s[i] = 'í';
                    s[i + 1] = NONE;
                    s[i + 2] = NONE;
                    LOGGER.debug(" > igh->í, i={} s={}", i, s);
                    return 2;
                }

                if (isConsonant(next) && next2 == 'e') {
                    // a{consonant}e -> á{consonant} and drop e
                    s[i] = 'í';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > i{consonant}e->í{consonant}, i={} s={}", i, s);
                    return 2;
                }

                if (next == 'l' && next2 == 'l') {
                    // Double l causes short i sound single l
                    s[i] = 'i';
                    s[i + 1] = 'l';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > ill->il, i={} s={}", i, s);
                    return 2;
                }

            }
            break;

            case 'o': {
                // ow sound, preceded by consonant and followed by
                // both, bozo, toss, mosh
                // bother
                char next = getNext(s, i);
                if (
                        isConsonant(getPrevious(s, i))
                                && (isHiss(next) || next == 'k')
                ) {
                    if (isVowel(next)) {
                        // in 'bother' o becomes ä due to vowel after hiss
                        s[i] = 'ä';
                        LOGGER.debug(" > o{hiss/k}{vowel}->ä, i={} s={}", i, s);
                    } else {
                        // in 'both' o becomes ö
                        s[i] = 'ö';
                        LOGGER.debug(" > o{hiss/k}->ö, i={} s={}", i, s);
                    }
                    return 1;
                }
                if (next == 'o') {
                    // oo
                    s[i] = 'ō';
                    s[i + 1] = NONE;
                    LOGGER.debug(" > oo->ō, i={} s={}", i, s);
                    return 1;
                }
                if (next == 'w') {
                    // ow
                    s[i] = 'ó';
                    s[i + 1] = NONE;
                    LOGGER.debug(" > ow->ó, i={} s={}", i, s);
                    return 1;
                }

                char next2 = getNextNext(s, i);
                if (next == 'u') {
                    char prev = getPrevious(s, i);
                    if (isShortPlosive(prev) && next2 == 'r') {
                        // {short consonant}our -> ûr
                        s[i] = 'û';
                        s[i + 1] = NONE;
                        LOGGER.debug(" > our->ûr, i={} s={}", i, s);
                    } else {
                        // ou
                        s[i] = 'œ';
                        s[i + 1] = NONE;
                        LOGGER.debug(" > ou->œ, i={} s={}", i, s);
                    }
                    return 1;
                }

                if (isConsonant(next) && next2 == 'e') {
                    // o{consonant}e -> ó{consonant} and drop e
                    s[i] = 'ó';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > o{consonant}e->ó{consonant}, i={} s={}", i, s);
                    return 2;
                }
            }
            break;

            case 's': {
                char next = getNext(s, i);
                if (next == 'i') {
                    // ti -> śi  (sh sound)
                    s[i] = 'ś';
                    LOGGER.debug(" > ti->śi, i={} s={}", i, s);
                    return 0;
                }
            }
            break;

            case 'u': {
                char next = getNext(s, i);
                char next2 = getNextNext(s, i);
                if (isConsonant(next) && next2 == 'e') {
                    // u{consonant}e -> ú{consonant} and drop e
                    s[i] = 'ú';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > u{consonant}e->ú{consonant}, i={} s={}", i, s);
                    return 2;
                }

                if (next == 'l' && next2 == 'l') {
                    // Double l causes long o sound single l
                    s[i] = 'ō';
                    s[i + 1] = 'l';
                    s[i + 2] = NONE;
                    LOGGER.debug(" > ull->ōl, i={} s={}", i, s);
                    return 2;
                }

                char prev = getPrevious(s, i);
                if (isConsonant(prev) && next == 'r') {
                    // {consonant}ur -> û
                    s[i] = 'û';
                    LOGGER.debug(" > {consonant}u{consonant}->û, i={} s={}", i, s);
                    return 0;
                }
            }
            break;

            case 'y': {
                char next = getNext(s, i);
                if (next == NONE && match('l', s, i, 'y')) {
                    // -ly becomes ë
                    s[i] = 'ë';
                    LOGGER.debug(" > -ly->ë, i={} s={}", i, s);
                    return 0;
                }
                if (isConsonant(next)) {
                    // y followed by consonant becomes í
                    s[i] = 'í';
                    LOGGER.debug(" > y{consonant}->í, i={} s={}", i, s);
                    return 0;
                } else if (next != NONE) {
                    // y followed by vowel becomes í and drops vowel
                    s[i] = 'í';
                    s[i + 1] = NONE;
                    LOGGER.debug(" > y{vowel}->í, i={} s={}", i, s);
                    return 1;
                }
            }
            break;
        }
        return 0;
    }

    /**
     * Match 0, +1 characters
     *
     * @param s  string
     * @param i  offset
     * @param c  p0 match
     * @param p1 p1 match
     * @return true if matches, false if not or out of bounds
     */
    private boolean match(char[] s, int i, char c, char p1) {
        return i < s.length - 1 && s[i] == c && s[i + 1] == p1;
    }

    /**
     * Match 0, +1, +2 characters
     *
     * @param s  string
     * @param i  offset
     * @param c  p0 match
     * @param p1 p1 match
     * @return true if matches, false if not or out of bounds
     */
    private boolean match(char[] s, int i, char c, char p1, char p2) {
        return i < s.length - 2 && s[i] == c && s[i + 1] == p1 && s[i + 2] == p2;
    }

    /**
     * Match -1, 0 characters
     *
     * @param s string
     * @param i offset
     * @param c p0 match
     * @return true if matches, false if not or out of bounds
     */
    private boolean match(char m1, char[] s, int i, char c) {
        return i > 0 && i < s.length && s[i] == c && s[i - 1] == m1;
    }

    /**
     * Match -1, 0, +1 characters
     *
     * @param s  string
     * @param i  offset
     * @param c  p0 match
     * @param p1 p1 match
     * @return true if matches, false if not or out of bounds
     */
    private boolean match(char m1, char[] s, int i, char c, char p1) {
        return i > 0 && i < s.length - 1 && s[i] == c && s[i + 1] == p1 && s[i - 1] == m1;
    }

    /**
     * Match -1, 0, +1 characters
     *
     * @param s  string
     * @param i  offset
     * @param c  p0 match
     * @param p1 p1 match
     * @return true if matches, false if not or out of bounds
     */
    private boolean match(char m1, char[] s, int i, char c, char p1, char p2) {
        return i > 0 && i < s.length - 2 && s[i] == c && s[i + 1] == p1 && s[i + 1] == p2 && s[i - 1] == m1;
    }

    /**
     * Get char at position
     *
     * @param s string
     * @param i offset
     * @return char at offset or NONE if out of bounds
     */
    private char getChar(char[] s, int i) {
        return i >= 0 && i < s.length ? s[i] : NONE;
    }

    /**
     * Extended vowel definition
     *
     * @param c character
     * @return true is considered a vowel
     */
    @Override
    protected boolean isVowel(char c) {
        return ALL_VOWELS.indexOf(c) >= 0;
    }

    /**
     * Consonants that produce a hissing sound (Fricatives and Affricates)
     *
     * @param c Character assumed lower case
     * @return true if hissing
     */
    protected boolean isHiss(char c) {
        return c == 's'
                || c == 'z'
                || c == 'x'
                || c == 'ß'    // ss
                || c == 'ż'    // th
                || c == 'ć'    // ch
                || c == 'ś'    // sh
                || c == 'ź'    // tz
                || c == 'š'    // sch
                || c == 'ž'    // sz
                ;
    }

    protected boolean isShortPlosive(char c) {
        return c == 'f'
                || c == 't'
                || c == 'p'
                ;
    }
}

/*
 * An enhanced transformer tries to differentiate sounds based on context
 * a0 - a - [short, exhale, eh] = cap, bag, man, had
 * a1 - à - [short, exhale, ah] = car, far, cab, cat, cafe
 * a2 - á - [macron] ay sound = day, hay, lay
 * a3 - â - = either, miser
 * a4 - ä - [diaeresis] sounds like ow = bother
 * a5 - ã -
 * a6 - å -
 * a7 - ā -
 * b0 - b -
 * c0 - c - (can be s or k)
 * c1 - ç -
 * c2 - ć - ch
 * c3 - č -
 * d0 - d -
 * e0 - e - [short] = set, head
 * e1 - è -
 * e2 - é - [macron] = rete
 * e3 - ê -
 * e4 - ë - [diaeresis] = meet, meat, feat
 * e5 - ē -
 * e6 - ė -
 * e7 - ę -
 * f0 - f -
 * g0 - g - (can sound like j0) (see n for ng sound) = go, big
 * g1 - G - (hard g) = go, big
 * h0 - h - (can be silent)
 * i0 - i - [short] = sip, ship, mitten, clipper
 * i1 - î -
 * i2 - ï - long i = isle
 * i3 - í - [macron] = site, ice
 * i4 - ī -
 * i5 - į -
 * i6 - ì -
 * j0 - j -
 * k0 - k -
 * l0 - l -
 * m0 - m -
 * n0 - n -
 * n1 - ñ -
 * n2 - ń -
 * o0 - o -
 * o1 - ô -
 * o2 - ö - [diaeresis] = mop, cop, stop
 * o3 - ò -
 * o4 - ó - [macron] = snow
 * o5 - œ - = loud, now
 * o6 - ø - = boy, koi, coin
 * o7 - ō - long o = good, could, food, pull
 * o8 - õ -
 * p0 - p -
 * q0 - q - sounds like kw (can sound like k without trailing u)
 * r0 - r -
 * s0 - s -
 * s1 - ß - long double s = hiss, kiss
 * s2 - ś - sh = hush, mush
 * s3 - š - szh = keisch
 * t0 - t - (short t, including double tt; see z for th) = hit, tin, mitten
 * u0 - u - [short] = cull, mull, mush, rust
 * u1 - û - fur, hurd
 * u2 - ü - [diaeresis] = moose, ooze, rule, youth
 * u3 - ù -
 * u4 - ú - [macron] = mute, lute, cure
 * u5 - ū - new, spew, hew = u as ew sound
 * v0 - v -
 * w0 - w - = well, work
 * w1 - W - = law, cow
 * x0 - x - sounds like ks = fix, hex
 * y0 - y -
 * z0 - z -
 * z1 - ż - tz sound = tzar
 * z2 - ź - th sound = with, this, wither
 * z3 - ž - sz sound = vision
 */