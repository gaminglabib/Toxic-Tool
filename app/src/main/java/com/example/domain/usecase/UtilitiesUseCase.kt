package com.example.domain.usecase

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class TimestampConversion(
    val epochSeconds: Long,
    val epochMillis: Long,
    val localDateTime: String,
    val utcDateTime: String,
    val iso8601: String
)

data class QueryParamItem(
    val key: String,
    val value: String
)

data class UnicodeCharInfo(
    val char: Char,
    val codePoint: Int,
    val hexCodePoint: String,
    val utf8BytesHex: String,
    val categoryName: String
)

class UtilitiesUseCase {

    fun convertEpoch(epochInput: String): TimestampConversion? {
        val clean = epochInput.trim()
        if (clean.isEmpty()) return null
        return try {
            val num = clean.toLong()
            val millis = if (clean.length <= 10) num * 1000L else num
            val seconds = millis / 1000L
            val date = Date(millis)

            val localFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            TimestampConversion(
                epochSeconds = seconds,
                epochMillis = millis,
                localDateTime = localFormat.format(date),
                utcDateTime = utcFormat.format(date),
                iso8601 = isoFormat.format(date)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun parseDateToEpoch(dateStr: String): Long? {
        val clean = dateStr.trim()
        if (clean.isEmpty()) return null
        val patterns = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd"
        )
        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                val parsed = sdf.parse(clean)
                if (parsed != null) return parsed.time / 1000L
            } catch (_: Exception) {}
        }
        return null
    }

    fun generateUuids(count: Int = 1, uppercase: Boolean = false, includeHyphens: Boolean = true): List<String> {
        val bounded = count.coerceIn(1, 20)
        return (1..bounded).map {
            var uuid = UUID.randomUUID().toString()
            if (!includeHyphens) {
                uuid = uuid.replace("-", "")
            }
            if (uppercase) uuid.uppercase() else uuid.lowercase()
        }
    }

    fun parseQueryString(inputUrlOrQuery: String): List<QueryParamItem> {
        val clean = inputUrlOrQuery.trim()
        if (clean.isEmpty()) return emptyList()

        val queryString = if (clean.contains("?")) {
            clean.substringAfter("?").substringBefore("#")
        } else {
            clean.substringBefore("#")
        }

        val pairs = queryString.split("&")
        val result = mutableListOf<QueryParamItem>()

        for (pair in pairs) {
            if (pair.isBlank()) continue
            val key = pair.substringBefore("=")
            val rawVal = if (pair.contains("=")) pair.substringAfter("=") else ""
            val decodedKey = try { URLDecoder.decode(key, StandardCharsets.UTF_8.name()) } catch (_: Exception) { key }
            val decodedVal = try { URLDecoder.decode(rawVal, StandardCharsets.UTF_8.name()) } catch (_: Exception) { rawVal }
            result.add(QueryParamItem(decodedKey, decodedVal))
        }

        return result
    }

    fun inspectUnicodeChars(text: String): List<UnicodeCharInfo> {
        return text.map { ch ->
            val cp = ch.code
            val hex = String.format("U+%04X", cp)
            val bytes = ch.toString().toByteArray(StandardCharsets.UTF_8)
            val bytesHex = bytes.joinToString(" ") { String.format("%02X", it) }
            val cat = Character.getType(ch)
            val catName = getCategoryName(cat)
            UnicodeCharInfo(
                char = ch,
                codePoint = cp,
                hexCodePoint = hex,
                utf8BytesHex = bytesHex,
                categoryName = catName
            )
        }
    }

    private fun getCategoryName(type: Int): String = when (type) {
        Character.UPPERCASE_LETTER.toInt() -> "Uppercase Letter"
        Character.LOWERCASE_LETTER.toInt() -> "Lowercase Letter"
        Character.DECIMAL_DIGIT_NUMBER.toInt() -> "Decimal Digit"
        Character.SPACE_SEPARATOR.toInt() -> "Space Separator"
        Character.LINE_SEPARATOR.toInt() -> "Line Separator"
        Character.DASH_PUNCTUATION.toInt() -> "Dash Punctuation"
        Character.START_PUNCTUATION.toInt() -> "Start Punctuation"
        Character.END_PUNCTUATION.toInt() -> "End Punctuation"
        Character.OTHER_PUNCTUATION.toInt() -> "Other Punctuation"
        Character.MATH_SYMBOL.toInt() -> "Math Symbol"
        Character.CURRENCY_SYMBOL.toInt() -> "Currency Symbol"
        Character.CONTROL.toInt() -> "Control Character"
        else -> "Character (Type $type)"
    }
}
