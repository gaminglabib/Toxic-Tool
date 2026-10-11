package com.example.domain.usecase

import java.security.SecureRandom
import kotlin.math.log2

data class RandomGenConfig(
    val length: Int = 24,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeDigits: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeAmbiguous: Boolean = false,
    val count: Int = 1
)

data class GeneratedTokenResult(
    val tokens: List<String>,
    val poolSize: Int,
    val entropyBits: Double,
    val strengthLabel: String
)

class RandomGeneratorUseCase {
    private val secureRandom = SecureRandom()

    private val upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowerChars = "abcdefghijklmnopqrstuvwxyz"
    private val digitChars = "0123456789"
    private val symbolChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private val ambiguousChars = setOf('l', '1', 'I', 'O', '0', 'o', '|', ';', ':', '.')

    fun generate(config: RandomGenConfig): GeneratedTokenResult {
        val boundedLen = config.length.coerceIn(4, 128)
        val boundedCount = config.count.coerceIn(1, 20)

        var pool = buildString {
            if (config.includeUppercase) append(upperChars)
            if (config.includeLowercase) append(lowerChars)
            if (config.includeDigits) append(digitChars)
            if (config.includeSymbols) append(symbolChars)
        }

        if (config.excludeAmbiguous) {
            pool = pool.filter { it !in ambiguousChars }
        }

        if (pool.isEmpty()) {
            pool = upperChars + lowerChars + digitChars
        }

        val poolSize = pool.length
        val entropy = boundedLen * (log2(poolSize.toDouble()))
        val strength = when {
            entropy < 40 -> "Weak (< 40 bits)"
            entropy < 64 -> "Moderate (40-64 bits)"
            entropy < 100 -> "Strong (64-100 bits)"
            else -> "Very Strong (100+ bits)"
        }

        val tokens = (1..boundedCount).map {
            val chars = CharArray(boundedLen)
            for (i in 0 until boundedLen) {
                chars[i] = pool[secureRandom.nextInt(poolSize)]
            }
            String(chars)
        }

        return GeneratedTokenResult(
            tokens = tokens,
            poolSize = poolSize,
            entropyBits = entropy,
            strengthLabel = strength
        )
    }
}
