package com.example.domain.usecase

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

sealed class JsonResult {
    data class Success(val formatted: String, val lineCount: Int, val byteCount: Int) : JsonResult()
    data class Error(val message: String, val errorSnippet: String? = null, val approximatePosition: Int? = null) : JsonResult()
}

class JsonUseCase {

    fun beautify(input: String, indentSpaces: Int = 2): JsonResult {
        if (input.isBlank()) {
            return JsonResult.Error("Input is empty")
        }
        return try {
            val trimmed = input.trim()
            val formatted = when {
                trimmed.startsWith("{") -> {
                    val obj = JSONObject(trimmed)
                    obj.toString(indentSpaces)
                }
                trimmed.startsWith("[") -> {
                    val arr = JSONArray(trimmed)
                    arr.toString(indentSpaces)
                }
                else -> {
                    // Try parsing as generic tokener
                    val tokener = JSONTokener(trimmed)
                    val value = tokener.nextValue()
                    when (value) {
                        is JSONObject -> value.toString(indentSpaces)
                        is JSONArray -> value.toString(indentSpaces)
                        else -> return JsonResult.Error("Invalid JSON root: Must be an object or array")
                    }
                }
            }
            JsonResult.Success(
                formatted = formatted,
                lineCount = formatted.lines().size,
                byteCount = formatted.toByteArray(Charsets.UTF_8).size
            )
        } catch (e: Exception) {
            parseJsonError(input, e)
        }
    }

    fun minify(input: String): JsonResult {
        if (input.isBlank()) {
            return JsonResult.Error("Input is empty")
        }
        return try {
            val trimmed = input.trim()
            val minified = when {
                trimmed.startsWith("{") -> JSONObject(trimmed).toString()
                trimmed.startsWith("[") -> JSONArray(trimmed).toString()
                else -> {
                    val tokener = JSONTokener(trimmed)
                    val value = tokener.nextValue()
                    when (value) {
                        is JSONObject -> value.toString()
                        is JSONArray -> value.toString()
                        else -> return JsonResult.Error("Invalid JSON root: Must be an object or array")
                    }
                }
            }
            JsonResult.Success(
                formatted = minified,
                lineCount = minified.lines().size,
                byteCount = minified.toByteArray(Charsets.UTF_8).size
            )
        } catch (e: Exception) {
            parseJsonError(input, e)
        }
    }

    fun validate(input: String): JsonResult {
        if (input.isBlank()) {
            return JsonResult.Error("Input is empty")
        }
        return try {
            val trimmed = input.trim()
            val typeStr = when {
                trimmed.startsWith("{") -> {
                    JSONObject(trimmed)
                    "Object"
                }
                trimmed.startsWith("[") -> {
                    JSONArray(trimmed)
                    "Array"
                }
                else -> {
                    val tokener = JSONTokener(trimmed)
                    val value = tokener.nextValue()
                    if (value !is JSONObject && value !is JSONArray) {
                        return JsonResult.Error("Root must be a valid JSON Object {} or Array []")
                    }
                    if (tokener.more()) {
                        val remaining = tokener.nextClean()
                        if (remaining != 0.toChar()) {
                            return JsonResult.Error("Unexpected trailing characters after valid JSON root")
                        }
                    }
                    if (value is JSONObject) "Object" else "Array"
                }
            }
            val lineCount = input.lines().size
            JsonResult.Success(
                formatted = "Valid JSON ($typeStr)",
                lineCount = lineCount,
                byteCount = input.toByteArray(Charsets.UTF_8).size
            )
        } catch (e: Exception) {
            parseJsonError(input, e)
        }
    }

    private fun parseJsonError(input: String, e: Exception): JsonResult.Error {
        val msg = e.message ?: "Invalid JSON syntax"
        // Extract position index if mentioned in "at character X"
        val regex = Regex("at character (\\d+)")
        val match = regex.find(msg)
        val pos = match?.groupValues?.getOrNull(1)?.toIntOrNull()
        val snippet = if (pos != null && pos >= 0 && pos <= input.length) {
            val start = (pos - 25).coerceAtLeast(0)
            val end = (pos + 25).coerceAtMost(input.length)
            input.substring(start, end).replace("\n", " ")
        } else null

        return JsonResult.Error(
            message = msg,
            errorSnippet = snippet,
            approximatePosition = pos
        )
    }
}
