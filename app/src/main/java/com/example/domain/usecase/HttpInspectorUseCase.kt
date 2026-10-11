package com.example.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.TimeUnit

data class HttpResponseResult(
    val statusCode: Int,
    val statusMessage: String,
    val durationMs: Long,
    val protocol: String,
    val isHttps: Boolean,
    val headers: List<Pair<String, String>>,
    val contentType: String?,
    val contentLength: Long,
    val bodyPreview: String,
    val isBodyTruncated: Boolean
)

sealed class HttpInspectionOutcome {
    data class Success(val response: HttpResponseResult) : HttpInspectionOutcome()
    data class SecurityBlocked(val reason: String) : HttpInspectionOutcome()
    data class NetworkError(val message: String, val errorType: String) : HttpInspectionOutcome()
}

class HttpInspectorUseCase {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .followRedirects(false) // Safe: do not blindly follow redirects to arbitrary locations
        .build()

    private val maxPreviewBytes = 64 * 1024 // 64 KB bounded preview

    suspend fun executeRequest(
        rawUrl: String,
        method: String = "GET"
    ): HttpInspectionOutcome = withContext(Dispatchers.IO) {
        val trimmedUrl = rawUrl.trim()
        if (trimmedUrl.isEmpty()) {
            return@withContext HttpInspectionOutcome.NetworkError("URL is empty", "Validation")
        }

        val urlWithScheme = when {
            trimmedUrl.startsWith("http://", ignoreCase = true) ||
                trimmedUrl.startsWith("https://", ignoreCase = true) -> trimmedUrl
            else -> "https://$trimmedUrl"
        }

        // Validate URL and enforce SSRF Protection
        val validation = validateDestination(urlWithScheme)
        if (validation != null) {
            return@withContext HttpInspectionOutcome.SecurityBlocked(validation)
        }

        val startTime = System.currentTimeMillis()
        try {
            val requestBuilder = Request.Builder()
                .url(urlWithScheme)
                .header("User-Agent", "ToxicToolbox/1.0 (Android; Offline-First)")
                .header("Accept", "*/*")

            when (method.uppercase()) {
                "HEAD" -> requestBuilder.head()
                else -> requestBuilder.get()
            }

            client.newCall(requestBuilder.build()).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                val headersList = mutableListOf<Pair<String, String>>()
                for (i in 0 until response.headers.size) {
                    headersList.add(response.headers.name(i) to response.headers.value(i))
                }

                val body = response.body
                val contentType = body?.contentType()?.toString()
                val contentLength = body?.contentLength() ?: -1L

                var previewText = ""
                var truncated = false

                if (body != null) {
                    val source = body.source()
                    source.request(maxPreviewBytes.toLong())
                    val buffer = source.buffer
                    val readBytes = minOf(buffer.size, maxPreviewBytes.toLong())
                    val bytes = buffer.clone().readByteArray(readBytes)
                    previewText = String(bytes, Charsets.UTF_8)
                    if (buffer.size > maxPreviewBytes || !source.exhausted()) {
                        truncated = true
                    }
                }

                HttpInspectionOutcome.Success(
                    HttpResponseResult(
                        statusCode = response.code,
                        statusMessage = response.message,
                        durationMs = duration,
                        protocol = response.protocol.toString(),
                        isHttps = urlWithScheme.startsWith("https://", ignoreCase = true),
                        headers = headersList,
                        contentType = contentType,
                        contentLength = contentLength,
                        bodyPreview = previewText,
                        isBodyTruncated = truncated
                    )
                )
            }
        } catch (e: java.net.UnknownHostException) {
            HttpInspectionOutcome.NetworkError("DNS resolution failed for host: ${e.message}", "DNS Error")
        } catch (e: java.net.SocketTimeoutException) {
            HttpInspectionOutcome.NetworkError("Connection timed out after 10s: ${e.message}", "Timeout")
        } catch (e: javax.net.ssl.SSLException) {
            HttpInspectionOutcome.NetworkError("TLS/SSL Handshake failed: ${e.message}", "TLS/Certificate")
        } catch (e: Exception) {
            HttpInspectionOutcome.NetworkError(e.message ?: "Request failed", "Connection Error")
        }
    }

    fun isPlainHttp(url: String): Boolean {
        return url.trim().startsWith("http://", ignoreCase = true)
    }

    private fun validateDestination(urlStr: String): String? {
        return try {
            val uri = URI(urlStr)
            val host = uri.host ?: return "Invalid hostname in URL"

            // SSRF Block: Localhost
            if (host.equals("localhost", ignoreCase = true) || host.endsWith(".localhost", ignoreCase = true)) {
                return "Requests to localhost are blocked for security."
            }

            // Cloud metadata endpoints
            if (host == "169.254.169.254" || host == "metadata.google.internal") {
                return "Requests to cloud metadata services are blocked."
            }

            // Resolve host to IP and check private subnet
            val addresses = InetAddress.getAllByName(host)
            for (addr in addresses) {
                if (addr.isLoopbackAddress) {
                    return "Requests to loopback IP (${addr.hostAddress}) are blocked."
                }
                if (addr.isSiteLocalAddress) {
                    return "Requests to private local network IP (${addr.hostAddress}) are blocked."
                }
                if (addr.isLinkLocalAddress) {
                    return "Requests to link-local IP (${addr.hostAddress}) are blocked."
                }
                if (addr.isAnyLocalAddress) {
                    return "Requests to wildcard local address are blocked."
                }
            }
            null
        } catch (e: Exception) {
            null // Allow network call to attempt standard DNS failure if resolution fails here
        }
    }
}
