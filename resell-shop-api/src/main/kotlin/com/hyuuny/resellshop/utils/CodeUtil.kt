package com.hyuuny.resellshop.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateOrderNumber(now: LocalDateTime = LocalDateTime.now()): String {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    val formattedDateTime = formatter.format(now)
    return "O_$formattedDateTime"
}
