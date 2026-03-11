package com.example.productbrowser.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

/**
 * Formats a Double to a string with [decimals] decimal places.
 * KMP-compatible — avoids JVM-only String.format.
 */
fun Double.formatDecimals(decimals: Int = 1): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    val intPart = rounded.toLong()
    val fracPart = abs(round((rounded - intPart) * factor).toInt())
    return if (decimals == 0) {
        "$intPart"
    } else {
        val fracStr = fracPart.toString().padStart(decimals, '0')
        "$intPart.$fracStr"
    }
}
