package com.example.domain.usecase

import android.util.Base64
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class JwtClaimItem(
    val key: String,
    val value: String,
    val description: String? = null
)

sealed class JwtInspectionResult {
    data class Success(
        val rawHeader: String,
        val prettyHeader: String,
        val rawPayload: String,
        val prettyPayload: String,
        val signatureHex: String,
        val algorithm: String?,
        val subject: String?,
        val issuer: String?,
        val audience: String?,
        val issuedAtFormatted: String?,
        val expiresAtFormatted: String?,
        val isExpired: Boolean?,
        val claims: List<JwtClaimItem>
    ) : JwtInspectionResult()

    data class Error(val message: String) : JwtInspectionResult()
}

class JwtUseCase {

    fun decodeToken(tokenInput: String): JwtInspectionResult {
        val token = tokenInput.trim()
        if (token.isEmpty()) return JwtInspectionResult.Error("JWT token is empty")

        val parts = token.split(".")
        if (parts.size < 2 || parts.size > 3) {
            return JwtInspectionResult.Error("Malformed JWT: Must contain 2 or 3 dot-separated segments (header.payload[.signature]). Got ${parts.size} segments.")
        }

        return try {
            val headerJson = decodeSegment(parts[0])
            val payloadJson = decodeSegment(parts[1])
            val signature = if (parts.size == 3) parts[2] else ""

            val headerObj = JSONObject(headerJson)
            val payloadObj = JSONObject(payloadJson)

            val prettyHeader = headerObj.toString(2)
            val prettyPayload = payloadObj.toString(2)

            val alg = headerObj.optString("alg", null)
            val sub = payloadObj.optString("sub", null)
            val iss = payloadObj.optString("iss", null)
            val aud = payloadObj.optString("aud", null)

            val expSeconds = if (payloadObj.has("exp")) payloadObj.optLong("exp") else null
            val iatSeconds = if (payloadObj.has("iat")) payloadObj.optLong("iat") else null

            val nowSeconds = System.currentTimeMillis() / 1000
            val isExpired = expSeconds?.let { it < nowSeconds }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val expFormatted = expSeconds?.let {
                val status = if (it < nowSeconds) " (EXPIRED)" else " (ACTIVE)"
                sdf.format(Date(it * 1000)) + status
            }

            val iatFormatted = iatSeconds?.let {
                sdf.format(Date(it * 1000))
            }

            val claimsList = mutableListOf<JwtClaimItem>()
            val keys = payloadObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = payloadObj.get(k).toString()
                val desc = getClaimDescription(k)
                claimsList.add(JwtClaimItem(k, v, desc))
            }

            JwtInspectionResult.Success(
                rawHeader = headerJson,
                prettyHeader = prettyHeader,
                rawPayload = payloadJson,
                prettyPayload = prettyPayload,
                signatureHex = signature,
                algorithm = alg,
                subject = sub,
                issuer = iss,
                audience = aud,
                issuedAtFormatted = iatFormatted,
                expiresAtFormatted = expFormatted,
                isExpired = isExpired,
                claims = claimsList
            )
        } catch (e: Exception) {
            JwtInspectionResult.Error("Failed to parse JWT: ${e.message}")
        }
    }

    private fun decodeSegment(base64UrlStr: String): String {
        val decoded = Base64.decode(base64UrlStr, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        return String(decoded, StandardCharsets.UTF_8)
    }

    private fun getClaimDescription(key: String): String? = when (key) {
        "iss" -> "Issuer (identifies who created the token)"
        "sub" -> "Subject (principal identifier / user ID)"
        "aud" -> "Audience (recipients this token is intended for)"
        "exp" -> "Expiration time (Unix timestamp in seconds)"
        "nbf" -> "Not Before time (token invalid before this timestamp)"
        "iat" -> "Issued At time (when token was generated)"
        "jti" -> "JWT ID (unique identifier for this token)"
        "roles" -> "Granted user roles / authorities"
        "email" -> "User email address"
        "name" -> "Full name of the principal"
        else -> null
    }
}
