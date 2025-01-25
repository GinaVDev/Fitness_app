package com.example.fitnessapp.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toTimeString(): String {
    val hours = this / 3600000
    val minutes = (this % 3600000) / 60000
    val seconds = (this % 60000) / 1000
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.toFormattedDateTime(): String {
    val instant = Instant.ofEpochMilli(this)
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")
    return zonedDateTime.format(formatter)
}
