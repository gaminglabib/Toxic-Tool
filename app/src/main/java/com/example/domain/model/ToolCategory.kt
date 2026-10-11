package com.example.domain.model

enum class ToolCategory(val title: String, val subtitle: String) {
    DATA_FORMATS("Data & Formats", "JSON and serialization tools"),
    CRYPTOGRAPHY("Cryptography & Security", "Encoding, hashing, and tokens"),
    TEXT_STRINGS("Text & Strings", "Text laboratory and inspection"),
    NETWORK("Network & Diagnostics", "DNS, HTTP, and connectivity probes"),
    UTILITIES("System & Time", "Timestamps, UUIDs, and quick utilities")
}
