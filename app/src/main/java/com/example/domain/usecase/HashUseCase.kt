package com.example.domain.usecase

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

data class HashResult(
    val sha256: String,
    val sha384: String,
    val sha512: String,
    val md5: String,
    val byteLength: Int
)

class HashUseCase {

    fun computeHashes(input: String, uppercase: Boolean = false): HashResult {
        val bytes = input.toByteArray(StandardCharsets.UTF_8)
        val sha256 = digest(bytes, "SHA-256", uppercase)
        val sha384 = digest(bytes, "SHA-384", uppercase)
        val sha512 = digest(bytes, "SHA-512", uppercase)
        val md5 = digest(bytes, "MD5", uppercase)

        return HashResult(
            sha256 = sha256,
            sha384 = sha384,
            sha512 = sha512,
            md5 = md5,
            byteLength = bytes.size
        )
    }

    fun verifyHash(computed: String, expected: String): Boolean {
        if (computed.isBlank() || expected.isBlank()) return false
        val cleanComp = computed.trim().replace(Regex("[^0-9a-fA-F]"), "")
        val cleanExp = expected.trim().replace(Regex("[^0-9a-fA-F]"), "")
        return cleanComp.equals(cleanExp, ignoreCase = true)
    }

    private fun digest(bytes: ByteArray, algorithm: String, uppercase: Boolean): String {
        val md = MessageDigest.getInstance(algorithm)
        val hash = md.digest(bytes)
        val sb = StringBuilder(hash.size * 2)
        for (b in hash) {
            val hex = String.format("%02x", b)
            sb.append(if (uppercase) hex.uppercase() else hex)
        }
        return sb.toString()
    }
}
