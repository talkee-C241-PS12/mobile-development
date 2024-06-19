package com.bangkit.talkee.utils

import com.bangkit.talkee.R

fun getFixedResources(key: String) : Int? {
    val fixedResources: Map<String, Int> = mapOf(
        "A" to R.drawable.hand_a,
        "B" to R.drawable.hand_b,
        "C" to R.drawable.hand_c,
        "D" to R.drawable.hand_d,
        "E" to R.drawable.hand_e,
        "F" to R.drawable.hand_f,
        "G" to R.drawable.hand_g,
        "H" to R.drawable.hand_h,
        "I" to R.drawable.hand_i,
        "J" to R.drawable.hand_j,
        "K" to R.drawable.hand_k,
        "L" to R.drawable.hand_l,
        "M" to R.drawable.hand_m,
        "N" to R.drawable.hand_n,
        "O" to R.drawable.hand_o,
        "P" to R.drawable.hand_p,
        "Q" to R.drawable.hand_q,
        "R" to R.drawable.hand_r,
        "S" to R.drawable.hand_s,
        "T" to R.drawable.hand_t,
        "U" to R.drawable.hand_u,
        "V" to R.drawable.hand_v,
        "W" to R.drawable.hand_w,
        "X" to R.drawable.hand_x,
        "Y" to R.drawable.hand_y,
        "Z" to R.drawable.hand_z,
        "a" to R.drawable.hand_a,
        "b" to R.drawable.hand_b,
        "c" to R.drawable.hand_c,
        "d" to R.drawable.hand_d,
        "e" to R.drawable.hand_e,
        "f" to R.drawable.hand_f,
        "g" to R.drawable.hand_g,
        "h" to R.drawable.hand_h,
        "i" to R.drawable.hand_i,
        "j" to R.drawable.hand_j,
        "k" to R.drawable.hand_k,
        "l" to R.drawable.hand_l,
        "m" to R.drawable.hand_m,
        "n" to R.drawable.hand_n,
        "o" to R.drawable.hand_o,
        "p" to R.drawable.hand_p,
        "q" to R.drawable.hand_q,
        "r" to R.drawable.hand_r,
        "s" to R.drawable.hand_s,
        "t" to R.drawable.hand_t,
        "u" to R.drawable.hand_u,
        "v" to R.drawable.hand_v,
        "w" to R.drawable.hand_w,
        "x" to R.drawable.hand_x,
        "y" to R.drawable.hand_y,
        "z" to R.drawable.hand_z,
    )

    return fixedResources[key]
}