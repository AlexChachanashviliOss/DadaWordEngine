package io.github.achacha.dada.engine.phonemix;

/**
 * An enhanced transformer tries to differentiate sounds based on context
 * a0 - a - [short] = cap, bag, man, had
 * a1 - à -
 * a2 - á - [macron] = day, hay, lay
 * a3 - â - = either, miser
 * a4 - ä - [diaeresis] = bother, heart
 * a5 - ã -
 * a6 - å -
 * a7 - ā -
 * e0 - e - [short] = set, head
 * e1 - è -
 * e2 - é - [macron] = bead
 * e3 - ê -
 * e4 - ë - [diaeresis] = meet, meat, feat
 * e5 - ē -
 * e6 - ė -
 * e7 - ę -
 * i0 - i - [short] = sip, ship, mitten, clipper
 * i1 - î -
 * i2 - ï -
 * i3 - í - [macron] = site, ice
 * i4 - ī -
 * i5 - į -
 * i6 - ì -
 * n0 - n -
 * n1 - ñ -
 * n2 - ń - ng = sing
 * o0 - o -
 * o1 - ô -
 * o2 - ö - [diaeresis] = mop, cop, stop
 * o3 - ò -
 * o4 - ó - [macron] = snow
 * o5 - œ - = loud, now
 * o6 - ø - = boy, koi, coin
 * o7 - ō -
 * o8 - õ -
 * u0 - u - [short] = cull, mull,
 * u1 - û - fur, hurd
 * u2 - ü - [diaeresis] = moose, ooze, rule, youth
 * u3 - ù - = pull,wood
 * u4 - ú -
 * u5 - ū -
 */
public class PhonemixEnhancedTransformer extends PhonemixTransformerBase {

    PhonemixEnhancedTransformer(PhoneticTransformerBuilder builder) {
        super(builder);
    }

    @Override
    protected String transformWord(char[] word) {
        return super.transformWord(word);
    }
}
