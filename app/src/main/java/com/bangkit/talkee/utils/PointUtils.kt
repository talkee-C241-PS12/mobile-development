package com.bangkit.talkee.utils

import java.text.DecimalFormat

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> {
            val millions = number / 1_000_000.0
            DecimalFormat("#.#").format(millions) + "jt"
        }
        number >= 1_000 -> {
            val thousands = number / 1_000.0
            DecimalFormat("#.#").format(thousands) + "rb"
        }
        else -> {
            number.toString()
        }
    }
}