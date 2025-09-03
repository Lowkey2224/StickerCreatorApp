package com.stickercreator.app.utils

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImageUtilsTest {

    @Test
    fun `calculateAspectRatio returns correct ratio`() {
        val ratio = calculateAspectRatio(1920, 1080)
        assertEquals(1.78f, ratio, 0.01f)
    }

    @Test
    fun `calculateCropDimensions returns square for equal dimensions`() {
        val (width, height) = calculateCropDimensions(1000, 1000)
        assertEquals(1000, width)
        assertEquals(1000, height)
    }

    @Test
    fun `isValidImageDimension returns true for valid dimensions`() {
        assertTrue(isValidImageDimension(512, 512))
        assertTrue(isValidImageDimension(1024, 768))
    }
}

// Simple utility functions for testing
fun calculateAspectRatio(width: Int, height: Int): Float {
    return width.toFloat() / height.toFloat()
}

fun calculateCropDimensions(width: Int, height: Int): Pair<Int, Int> {
    val minDimension = kotlin.math.min(width, height)
    return Pair(minDimension, minDimension)
}

fun isValidImageDimension(width: Int, height: Int): Boolean {
    return width > 0 && height > 0 && width <= 4096 && height <= 4096
}
