package com.example.domain.usecase

import java.nio.charset.StandardCharsets

data class TextStatistics(
    val characterCount: Int,
    val characterCountNoSpaces: Int,
    val wordCount: Int,
    val lineCount: Int,
    val whitespaceCount: Int,
    val byteSizeUtf8: Int
)

data class DiffLine(
    val type: DiffType,
    val text: String,
    val lineNumber: Int
)

enum class DiffType {
    SAME, ADDED, REMOVED
}

data class DiffSummary(
    val additions: Int,
    val deletions: Int,
    val identical: Int,
    val lines: List<DiffLine>
)

class TextLabUseCase {

    fun calculateStats(text: String): TextStatistics {
        val chars = text.length
        val charsNoSpaces = text.count { !it.isWhitespace() }
        val whitespaces = text.count { it.isWhitespace() }
        val words = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
        val lines = if (text.isEmpty()) 0 else text.lines().size
        val bytes = text.toByteArray(StandardCharsets.UTF_8).size

        return TextStatistics(
            characterCount = chars,
            characterCountNoSpaces = charsNoSpaces,
            wordCount = words,
            lineCount = lines,
            whitespaceCount = whitespaces,
            byteSizeUtf8 = bytes
        )
    }

    fun toUpperCase(text: String): String = text.uppercase()

    fun toLowerCase(text: String): String = text.lowercase()

    fun toTitleCase(text: String): String {
        return text.split(Regex("(?<=\\s)|(?=\\s)")).joinToString("") { word ->
            if (word.isBlank()) word
            else word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    fun toCamelCase(text: String): String {
        val words = text.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return ""
        return words.first().lowercase() + words.drop(1).joinToString("") { w ->
            w.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    fun toSnakeCase(text: String): String {
        return text.trim()
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(Regex("[\\s-]+"), "_")
            .lowercase()
    }

    fun toKebabCase(text: String): String {
        return text.trim()
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .replace(Regex("[\\s_]+"), "-")
            .lowercase()
    }

    fun trimExtraWhitespace(text: String): String {
        return text.lines()
            .map { it.trim().replace(Regex("[ \\t]+"), " ") }
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    fun removeDuplicateLines(text: String): String {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<String>()
        for (line in text.lines()) {
            if (seen.add(line)) {
                result.add(line)
            }
        }
        return result.joinToString("\n")
    }

    fun sortLines(text: String, descending: Boolean = false): String {
        val lines = text.lines()
        val sorted = if (descending) lines.sortedDescending() else lines.sorted()
        return sorted.joinToString("\n")
    }

    fun findAndReplace(
        text: String,
        find: String,
        replace: String,
        matchCase: Boolean = true,
        useRegex: Boolean = false
    ): String {
        if (find.isEmpty()) return text
        return try {
            if (useRegex) {
                val options = if (matchCase) emptySet() else setOf(RegexOption.IGNORE_CASE)
                text.replace(Regex(find, options), replace)
            } else {
                text.replace(find, replace, ignoreCase = !matchCase)
            }
        } catch (e: Exception) {
            text
        }
    }

    fun computeDiff(textA: String, textB: String): DiffSummary {
        val linesA = if (textA.isEmpty()) emptyList() else textA.lines()
        val linesB = if (textB.isEmpty()) emptyList() else textB.lines()

        val diffList = mutableListOf<DiffLine>()
        var additions = 0
        var deletions = 0
        var identical = 0

        val maxLen = maxOf(linesA.size, linesB.size)
        var lineNum = 1

        var i = 0
        var j = 0
        while (i < linesA.size || j < linesB.size) {
            val a = linesA.getOrNull(i)
            val b = linesB.getOrNull(j)
            if (a == b && a != null) {
                diffList.add(DiffLine(DiffType.SAME, a, lineNum++))
                identical++
                i++
                j++
            } else {
                if (a != null && (b == null || !linesB.drop(j).contains(a))) {
                    diffList.add(DiffLine(DiffType.REMOVED, a, lineNum++))
                    deletions++
                    i++
                } else if (b != null) {
                    diffList.add(DiffLine(DiffType.ADDED, b, lineNum++))
                    additions++
                    j++
                } else {
                    i++
                    j++
                }
            }
        }

        return DiffSummary(
            additions = additions,
            deletions = deletions,
            identical = identical,
            lines = diffList
        )
    }
}
