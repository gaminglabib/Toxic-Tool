package com.example.domain.model

data class ToolItem(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val category: ToolCategory,
    val tags: List<String>,
    val isNetworkRequired: Boolean = false,
    val isSensitiveSecret: Boolean = false
)

object ToolRegistry {
    val ALL_TOOLS: List<ToolItem> = listOf(
        ToolItem(
            id = "json_studio",
            name = "JSON Studio",
            shortDescription = "Validate, beautify, and minify JSON data",
            fullDescription = "Inspect JSON structure, format with custom indentation, minify for production, and pinpoint syntax errors with exact line locations.",
            category = ToolCategory.DATA_FORMATS,
            tags = listOf("json", "format", "beautify", "minify", "validate", "syntax")
        ),
        ToolItem(
            id = "encoding_studio",
            name = "Encoding Studio",
            shortDescription = "Base64, URL, Hex, Binary & Decimal conversion",
            fullDescription = "Multi-radix data transformations including Base64 UTF-8, percent URL encoding, hexadecimal raw dumps, and binary-to-decimal converters.",
            category = ToolCategory.CRYPTOGRAPHY,
            tags = listOf("base64", "hex", "url", "binary", "decimal", "encode", "decode")
        ),
        ToolItem(
            id = "hash_studio",
            name = "Hash Studio",
            shortDescription = "Cryptographic digests: SHA-256, 384, 512",
            fullDescription = "Generate deterministic one-way cryptographic hashes using standard Android crypto providers with checksum comparison.",
            category = ToolCategory.CRYPTOGRAPHY,
            tags = listOf("hash", "sha256", "sha384", "sha512", "digest", "checksum")
        ),
        ToolItem(
            id = "text_lab",
            name = "Text Laboratory",
            shortDescription = "Live stats, text transforms & diff comparison",
            fullDescription = "Analyze words, characters, and lines in real-time. Apply case conversions, clean duplicate lines, find & replace, and compare text differences.",
            category = ToolCategory.TEXT_STRINGS,
            tags = listOf("text", "words", "lines", "case", "diff", "replace", "sort")
        ),
        ToolItem(
            id = "random_gen",
            name = "Secure Random",
            shortDescription = "Cryptographically secure string generator",
            fullDescription = "Generate non-deterministic tokens and random secrets using java.security.SecureRandom with configurable charset and entropy meter.",
            category = ToolCategory.CRYPTOGRAPHY,
            tags = listOf("random", "token", "securerandom", "entropy", "secret"),
            isSensitiveSecret = true
        ),
        ToolItem(
            id = "jwt_inspector",
            name = "JWT Inspector",
            shortDescription = "Decode & inspect JSON Web Tokens locally",
            fullDescription = "Parse header and payload claims safely without remote verification or external network calls. Check token expiration and timing claims.",
            category = ToolCategory.CRYPTOGRAPHY,
            tags = listOf("jwt", "token", "claims", "bearer", "decode", "auth"),
            isSensitiveSecret = true
        ),
        ToolItem(
            id = "http_inspector",
            name = "HTTP Inspector",
            shortDescription = "Inspect endpoints, headers & status codes",
            fullDescription = "Issue live HTTP/HTTPS test probes with timeouts, SSRF guardrails, response latency measurements, headers inspection, and bounded body preview.",
            category = ToolCategory.NETWORK,
            tags = listOf("http", "https", "request", "api", "curl", "headers", "status"),
            isNetworkRequired = true
        ),
        ToolItem(
            id = "dns_lookup",
            name = "DNS Lookup",
            shortDescription = "Resolve domain names to IPv4 & IPv6 addresses",
            fullDescription = "Query system DNS resolver asynchronously for hostname address mappings and canonical domain verification.",
            category = ToolCategory.NETWORK,
            tags = listOf("dns", "ip", "ipv4", "ipv6", "hostname", "domain", "resolve"),
            isNetworkRequired = true
        ),
        ToolItem(
            id = "network_diag",
            name = "Network Diagnostics",
            shortDescription = "Real-time connectivity, latency & transport test",
            fullDescription = "Analyze local interface states, detect cellular/Wi-Fi/VPN routes, check validated internet reachability, and measure roundtrip ping.",
            category = ToolCategory.NETWORK,
            tags = listOf("network", "wifi", "cellular", "ping", "connectivity", "internet"),
            isNetworkRequired = true
        ),
        ToolItem(
            id = "local_utils",
            name = "Local Utilities",
            shortDescription = "Unix epoch converter, UUIDs & URL query parser",
            fullDescription = "Developer toolbox essentials: Unix epoch seconds/millis converter, v4 UUID generator, URL query parameter parser, and Unicode character inspector.",
            category = ToolCategory.UTILITIES,
            tags = listOf("epoch", "timestamp", "date", "uuid", "query", "url", "unicode")
        )
    )

    fun findById(id: String): ToolItem? = ALL_TOOLS.find { it.id == id }
}
