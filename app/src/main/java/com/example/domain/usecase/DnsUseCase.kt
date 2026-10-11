package com.example.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException

data class DnsRecordItem(
    val ip: String,
    val type: String, // IPv4 or IPv6
    val canonicalName: String
)

sealed class DnsResult {
    data class Success(
        val hostname: String,
        val records: List<DnsRecordItem>,
        val lookupDurationMs: Long
    ) : DnsResult()

    data class Error(val message: String) : DnsResult()
}

class DnsUseCase {

    suspend fun resolveHost(hostInput: String): DnsResult = withContext(Dispatchers.IO) {
        val cleanHost = hostInput.trim()
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore("/")
            .substringBefore(":")

        if (cleanHost.isEmpty()) {
            return@withContext DnsResult.Error("Hostname is empty")
        }

        // Basic domain validation
        if (cleanHost.contains(" ") || cleanHost.length > 253) {
            return@withContext DnsResult.Error("Invalid domain format: contains spaces or exceeds 253 characters")
        }

        val startTime = System.currentTimeMillis()
        try {
            // Apply 8-second timeout
            withTimeout(8000L) {
                val addresses = InetAddress.getAllByName(cleanHost)
                val duration = System.currentTimeMillis() - startTime

                val records = addresses.map { addr ->
                    val type = when (addr) {
                        is Inet4Address -> "IPv4 (A)"
                        is Inet6Address -> "IPv6 (AAAA)"
                        else -> "IP"
                    }
                    DnsRecordItem(
                        ip = addr.hostAddress ?: "",
                        type = type,
                        canonicalName = addr.canonicalHostName
                    )
                }

                DnsResult.Success(
                    hostname = cleanHost,
                    records = records,
                    lookupDurationMs = duration
                )
            }
        } catch (e: UnknownHostException) {
            DnsResult.Error("Host not found (UnknownHostException). Check spelling or connection.")
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            DnsResult.Error("DNS resolution timed out after 8s.")
        } catch (e: Exception) {
            DnsResult.Error("DNS lookup failed: ${e.message}")
        }
    }
}
