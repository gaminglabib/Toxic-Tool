package com.example.domain.usecase

import android.util.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class TransformResult {
    data class Success(val result: String, val info: String? = null) : TransformResult()
    data class Error(val message: String) : TransformResult()
}

class EncodingUseCase {

    fun base64Encode(input: String, urlSafe: Boolean = false): TransformResult {
        if (input.isEmpty()) return TransformResult.Success("", "0 bytes")
        return try {
            val flags = if (urlSafe) Base64.URL_SAFE or Base64.NO_WRAP else Base64.NO_WRAP
            val bytes = input.toByteArray(StandardCharsets.UTF_8)
            val encoded = Base64.encodeToString(bytes, flags)
            TransformResult.Success(encoded, "${bytes.size} bytes -> ${encoded.length} chars")
        } catch (e: Exception) {
            TransformResult.Error("Base64 encode failed: ${e.message}")
        }
    }

    fun base64Decode(input: String, urlSafe: Boolean = false): TransformResult {
        val clean = input.trim()
        if (clean.isEmpty()) return TransformResult.Success("", "0 bytes")
        return try {
            val flags = if (urlSafe) Base64.URL_SAFE else Base64.DEFAULT
            val decodedBytes = Base64.decode(clean, flags)
            val decodedStr = String(decodedBytes, StandardCharsets.UTF_8)
            TransformResult.Success(decodedStr, "${decodedBytes.size} bytes decoded")
        } catch (e: Exception) {
            TransformResult.Error("Invalid Base64 string: ${e.message}")
        }
    }

    fun urlEncode(input: String): TransformResult {
        if (input.isEmpty()) return TransformResult.Success("", "0 chars")
        return try {
            val encoded = URLEncoder.encode(input, StandardCharsets.UTF_8.name())
            TransformResult.Success(encoded, "${encoded.length} characters")
        } catch (e: Exception) {
            TransformResult.Error("URL encoding failed: ${e.message}")
        }
    }

    fun urlDecode(input: String): TransformResult {
        if (input.isEmpty()) return TransformResult.Success("", "0 chars")
        return try {
            val decoded = URLDecoder.decode(input, StandardCharsets.UTF_8.name())
            TransformResult.Success(decoded, "${decoded.length} characters")
        } catch (e: Exception) {
            TransformResult.Error("URL decoding failed: ${e.message}")
        }
    }

    fun hexEncode(input: String, delimiter: String = " "): TransformResult {
        if (input.isEmpty()) return TransformResult.Success("", "0 bytes")
        return try {
            val bytes = input.toByteArray(StandardCharsets.UTF_8)
            val hex = bytes.joinToString(delimiter) { String.format("%02X", it) }
            TransformResult.Success(hex, "${bytes.size} bytes (${hex.length} hex chars)")
        } catch (e: Exception) {
            TransformResult.Error("Hex encode failed: ${e.message}")
        }
    }

    fun hexDecode(input: String): TransformResult {
        val clean = input.replace(Regex("[^0-9a-fA-F]"), "")
        if (clean.isEmpty()) return TransformResult.Success("", "0 bytes")
        if (clean.length % 2 != 0) {
            return TransformResult.Error("Hex string length must be even (currently ${clean.length} digits)")
        }
        return try {
            val bytes = ByteArray(clean.length / 2)
            for (i in clean.indices step 2) {
                bytes[i / 2] = clean.substring(i, i + 2).toInt(16).toByte()
            }
            val text = String(bytes, StandardCharsets.UTF_8)
            TransformResult.Success(text, "${bytes.size} bytes decoded")
        } catch (e: Exception) {
            TransformResult.Error("Invalid Hex sequence: ${e.message}")
        }
    }

    fun textToBinary(input: String): TransformResult {
        if (input.isEmpty()) return TransformResult.Success("", "0 bytes")
        return try {
            val bytes = input.toByteArray(StandardCharsets.UTF_8)
            val binary = bytes.joinToString(" ") { b ->
                val str = Integer.toBinaryString((b.toInt() and 0xFF))
                str.padStart(8, '0')
            }
            TransformResult.Success(binary, "${bytes.size} bytes (${bytes.size * 8} bits)")
        } catch (e: Exception) {
            TransformResult.Error("Binary encode failed: ${e.message}")
        }
    }

    fun binaryToText(input: String): TransformResult {
        val clean = input.trim()
        if (clean.isEmpty()) return TransformResult.Success("", "0 bytes")
        val tokens = clean.split(Regex("\\s+"))
        return try {
            val bytes = ByteArray(tokens.size)
            for (i in tokens.indices) {
                val token = tokens[i]
                if (!token.matches(Regex("^[01]{1,8}$"))) {
                    return TransformResult.Error("Invalid binary token: '$token'. Expected 1-8 binary digits [0,1]")
                }
                bytes[i] = token.toInt(2).toByte()
            }
            val text = String(bytes, StandardCharsets.UTF_8)
            TransformResult.Success(text, "${bytes.size} bytes decoded")
        } catch (e: Exception) {
            TransformResult.Error("Binary decode failed: ${e.message}")
        }
    }

    fun decimalToBinaryAndHex(decimalInput: String): TransformResult {
        val clean = decimalInput.trim()
        if (clean.isEmpty()) return TransformResult.Error("Enter a decimal integer")
        return try {
            val value = clean.toLong()
            val binary = java.lang.Long.toBinaryString(value)
            val hex = java.lang.Long.toHexString(value).uppercase()
            val octal = java.lang.Long.toOctalString(value)
            val out = "Decimal: $value\nBinary: 0b$binary\nHex: 0x$hex\nOctal: 0o$octal"
            TransformResult.Success(out, "Converted numeric base")
        } catch (e: Exception) {
            TransformResult.Error("Number format error: ${e.message}")
        }
    }
}
