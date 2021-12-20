package io.github.achacha.dada.engine.phonemix;


public class PhonemixEnhancedTransformer extends PhonemixTransformerBase {

    PhonemixEnhancedTransformer(PhoneticTransformerBuilder builder) {
        super(builder);
    }

    // TODO: Coming soon
}

/*
 * An enhanced transformer tries to differentiate sounds based on context
 * a0 - a - [short] = cap, bag, man, had
 * a1 - à -
 *+ a2 - á - [macron] ay sound = day, hay, lay
 * a3 - â - = either, miser
 *+ a4 - ä - [diaeresis] sounds like ow = bother, heart
 * a5 - ã -
 * a6 - å -
 * a7 - ā -
 * b0 - b -
 * c0 - c - (can be s or k)
 * c1 - ć - ch
 * c2 - č - tz
 * d0 - d -
 * e0 - e - [short] = set, head
 * e1 - è -
 * e2 - é - [macron] = bead
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
 * i2 - ï -
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
 * o7 - ō -
 * o8 - õ -
 * p0 - p -
 * q0 - q - sounds like kw (can sound like k without trailing u)
 * r0 - r -
 * s0 - s -
 * s1 - ß - long double s = hiss, kiss
 * s2 - ś - sh = hush, mush
 * s3 - š - szh = keisch,
 * t0 - t - (short t, including double tt; see z for th) = hit, tin, mitten
 * u0 - u - [short] = cull, mull,
 * u1 - û - fur, hurd
 * u2 - ü - [diaeresis] = moose, ooze, rule, youth
 * u3 - ù - = pull,wood
 * u4 - ú -
 * u5 - ū -
 * v0 - v -
 * w0 - w - = well, work
 * w1 - W - = law, cow
 * x0 - x - sounds like ks = fix, hex
 * y0 - y -
 * z0 - z -
 * z1 - ż - th sound = with, this, wither
 * z2 - ź
 * z3 - ž - sz sound = vision
 */