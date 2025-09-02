package com.stickercreator.app.utils

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebPValidationTest {

    @Test
    fun `isValidWebPSignature returns true for valid WebP header`() {
        // Given: Valid WebP file signature
        val validWebPHeader = "RIFF\u0000\u0000\u0000\u0000WEBP".toByteArray()

        // When & Then
        assertTrue(isValidWebPSignature(validWebPHeader))
    }

    @Test
    fun `isValidWebPSignature returns false for invalid header`() {
        // Given: Invalid file signatures
        val jpegHeader = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
        val pngHeader = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
        val emptyHeader = byteArrayOf()

        // When & Then
        assertFalse(isValidWebPSignature(jpegHeader))
        assertFalse(isValidWebPSignature(pngHeader))
        assertFalse(isValidWebPSignature(emptyHeader))
    }

    @Test
    fun `isValidImageDimensions returns true for 512x512`() {
        assertTrue(isValidImageDimensions(512, 512))
    }

    @Test
    fun `isValidImageDimensions returns false for incorrect dimensions`() {
        assertFalse(isValidImageDimensions(256, 256))
        assertFalse(isValidImageDimensions(1024, 1024))
        assertFalse(isValidImageDimensions(512, 256))
        assertFalse(isValidImageDimensions(256, 512))
    }

    @Test
    fun `calculateCompressionRatio returns expected values`() {
        val originalSize = 1000L
        val compressedSize = 500L
        val ratio = calculateCompressionRatio(originalSize, compressedSize)

        // Should be 50% compression (0.5 ratio)
        assertTrue(ratio > 0.49 && ratio < 0.51, "Expected ~0.5, got $ratio")
    }
}

// Utility functions for WebP validation
fun isValidWebPSignature(data: ByteArray): Boolean {
    if (data.size < 12) return false

    val header = String(data.take(12).toByteArray())
    return header.startsWith("RIFF") && header.contains("WEBP")
}

fun isValidImageDimensions(width: Int, height: Int): Boolean {
    return width == 512 && height == 512
}

fun calculateCompressionRatio(originalSize: Long, compressedSize: Long): Double {
    if (originalSize == 0L) return 0.0
    return compressedSize.toDouble() / originalSize.toDouble()
}
