package com.example.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class NetworkDiagnosticState(
    val isConnected: Boolean,
    val hasValidatedInternet: Boolean,
    val transportType: String,
    val isMetered: Boolean,
    val linkDownstreamBandwidthKbps: Int?,
    val timestamp: String,
    val probeStatus: String? = null,
    val probeLatencyMs: Long? = null
)

class NetworkDiagnosticsUseCase(private val context: Context) {

    private val probeClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    fun checkInterfaceStatus(): NetworkDiagnosticState {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork
        val caps = activeNetwork?.let { cm.getNetworkCapabilities(it) }

        val isConnected = caps != null && (
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        )
        val hasValidatedInternet = caps != null && (
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        )

        val transport = when {
            caps == null -> "No active network"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi (WLAN)"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular (Mobile Data)"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN Active"
            else -> "Other Network Transport"
        }

        val isMetered = cm?.isActiveNetworkMetered == true
        val speed = caps?.linkDownstreamBandwidthKbps

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return NetworkDiagnosticState(
            isConnected = isConnected,
            hasValidatedInternet = hasValidatedInternet,
            transportType = transport,
            isMetered = isMetered,
            linkDownstreamBandwidthKbps = if (speed != null && speed > 0) speed else null,
            timestamp = sdf.format(Date())
        )
    }

    suspend fun performHttpsProbe(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            // Safe probe using standard Cloudflare HTTPS 204 endpoint
            val req = Request.Builder()
                .url("https://1.1.1.1/cdn-cgi/trace")
                .head()
                .build()

            probeClient.newCall(req).execute().use { res ->
                val duration = System.currentTimeMillis() - start
                (res.isSuccessful || res.code in 200..399) to duration
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - start
            false to duration
        }
    }
}
