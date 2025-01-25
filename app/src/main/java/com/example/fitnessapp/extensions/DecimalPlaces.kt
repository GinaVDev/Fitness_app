package com.example.fitnessapp.extensions

import java.text.DecimalFormat

fun Double.formatToDecimalPlaces(pattern: String = "#.##"): String {
    val formatter = DecimalFormat(pattern)
    return formatter.format(this)
}

fun Float.formatToDecimalPlaces(pattern: String = "#.##"): String {
    return this.toDouble().formatToDecimalPlaces(pattern)
}
