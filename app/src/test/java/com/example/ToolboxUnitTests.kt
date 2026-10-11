package com.example

import com.example.domain.usecase.DiffType
import com.example.domain.usecase.DnsUseCase
import com.example.domain.usecase.EncodingUseCase
import com.example.domain.usecase.HashUseCase
import com.example.domain.usecase.JsonResult
import com.example.domain.usecase.JsonUseCase
import com.example.domain.usecase.JwtInspectionResult
import com.example.domain.usecase.JwtUseCase
import com.example.domain.usecase.RandomGenConfig
import com.example.domain.usecase.RandomGeneratorUseCase
import com.example.domain.usecase.TextLabUseCase
import com.example.domain.usecase.TransformResult
import com.example.domain.usecase.UtilitiesUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ToolboxUnitTests {

    private val jsonUseCase = JsonUseCase()
    private val encodingUseCase = EncodingUseCase()
    private val hashUseCase = HashUseCase()
    private val textLabUseCase = TextLabUseCase()
    private val randomGeneratorUseCase = RandomGeneratorUseCase()
    private val jwtUseCase = JwtUseCase()
    private val utilitiesUseCase = UtilitiesUseCase()

    @Test
    fun testJsonBeautifyAndMinify() {
        val raw = """{"foo":"bar","num":42,"list":[1,2]}"""
        val beautified = jsonUseCase.beautify(raw, indentSpaces = 2)
        assertTrue(beautified is JsonResult.Success)
        val formatted = (beautified as JsonResult.Success).formatted
        assertTrue(formatted.contains("\n"))

        val minified = jsonUseCase.minify(formatted)
        assertTrue(minified is JsonResult.Success)
        assertEquals(raw, (minified as JsonResult.Success).formatted)
    }

    @Test
    fun testJsonValidation() {
        val valid = """{"status":"ok"}"""
        val validRes = jsonUseCase.validate(valid)
        assertTrue(validRes is JsonResult.Success)

        val invalid = """{"status":"""
        val invalidRes = jsonUseCase.validate(invalid)
        assertTrue(invalidRes is JsonResult.Error)
    }

    @Test
    fun testBase64EncodingAndDecoding() {
        val input = "Toxic Toolbox Android 9+"
        val enc = encodingUseCase.base64Encode(input)
        assertTrue(enc is TransformResult.Success)
        val b64 = (enc as TransformResult.Success).result

        val dec = encodingUseCase.base64Decode(b64)
        assertTrue(dec is TransformResult.Success)
        assertEquals(input, (dec as TransformResult.Success).result)
    }

    @Test
    fun testHexEncodingAndDecoding() {
        val input = "Hello World"
        val enc = encodingUseCase.hexEncode(input, delimiter = " ")
        assertTrue(enc is TransformResult.Success)
        val hex = (enc as TransformResult.Success).result

        val dec = encodingUseCase.hexDecode(hex)
        assertTrue(dec is TransformResult.Success)
        assertEquals(input, (dec as TransformResult.Success).result)
    }

    @Test
    fun testBinaryConversion() {
        val input = "AB"
        val enc = encodingUseCase.textToBinary(input)
        assertTrue(enc is TransformResult.Success)
        val bin = (enc as TransformResult.Success).result
        // 'A' is 65 (01000001), 'B' is 66 (01000010)
        assertEquals("01000001 01000010", bin)

        val dec = encodingUseCase.binaryToText(bin)
        assertTrue(dec is TransformResult.Success)
        assertEquals(input, (dec as TransformResult.Success).result)
    }

    @Test
    fun testHashComputationAndVerification() {
        val text = "test"
        // Expected SHA-256 for "test" is 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08
        val hashes = hashUseCase.computeHashes(text)
        assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", hashes.sha256)

        assertTrue(hashUseCase.verifyHash(hashes.sha256, "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
        assertFalse(hashUseCase.verifyHash(hashes.sha256, "wrong_hash"))
    }

    @Test
    fun testTextLaboratoryTransformsAndStats() {
        val text = "hello world\nsecond line"
        val stats = textLabUseCase.calculateStats(text)
        assertEquals(23, stats.characterCount)
        assertEquals(4, stats.wordCount)
        assertEquals(2, stats.lineCount)

        assertEquals("HELLO WORLD\nSECOND LINE", textLabUseCase.toUpperCase(text))
        assertEquals("helloWorldSecondLine", textLabUseCase.toCamelCase("hello world second line"))
        assertEquals("hello_world_second_line", textLabUseCase.toSnakeCase("hello world second line"))
        assertEquals("hello-world-second-line", textLabUseCase.toKebabCase("hello world second line"))
    }

    @Test
    fun testTextDiff() {
        val original = "line1\nline2\nline3"
        val modified = "line1\nline2 modified\nline3"
        val diff = textLabUseCase.computeDiff(original, modified)

        assertEquals(1, diff.additions)
        assertEquals(1, diff.deletions)
        assertEquals(2, diff.identical)
    }

    @Test
    fun testSecureRandomGeneration() {
        val config = RandomGenConfig(length = 32, count = 3)
        val result = randomGeneratorUseCase.generate(config)

        assertEquals(3, result.tokens.size)
        assertEquals(32, result.tokens[0].length)
        assertTrue(result.entropyBits > 100)
    }

    @Test
    fun testJwtInspection() {
        val validJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val outcome = jwtUseCase.decodeToken(validJwt)

        assertTrue(outcome is JwtInspectionResult.Success)
        val success = outcome as JwtInspectionResult.Success
        assertEquals("HS256", success.algorithm)
        assertEquals("1234567890", success.subject)
        assertTrue(success.claims.any { it.key == "name" && it.value == "John Doe" })

        // Malformed token
        val invalidOutcome = jwtUseCase.decodeToken("invalid.token")
        assertTrue(invalidOutcome is JwtInspectionResult.Error)
    }

    @Test
    fun testUtilitiesEpochAndUuid() {
        val epoch = 1700000000L
        val conversion = utilitiesUseCase.convertEpoch(epoch.toString())
        assertNotNull(conversion)
        assertEquals(epoch, conversion?.epochSeconds)

        val uuids = utilitiesUseCase.generateUuids(5, uppercase = true, includeHyphens = true)
        assertEquals(5, uuids.size)
        assertTrue(uuids[0].contains("-"))
        assertEquals(uuids[0], uuids[0].uppercase())

        val query = "https://example.com?query=toxic&tag=dev"
        val parsed = utilitiesUseCase.parseQueryString(query)
        assertEquals(2, parsed.size)
        assertEquals("query", parsed[0].key)
        assertEquals("toxic", parsed[0].value)
        assertEquals("tag", parsed[1].key)
        assertEquals("dev", parsed[1].value)
    }
}
