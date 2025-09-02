package com.stickercreator.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ImageRepositoryIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var imageRepository: ImageRepository

    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun saveImageAsWebP_createsCorrectWebPImageWith512x512Dimensions() = runTest {
        // Given: Create a test bitmap with different dimensions
        val originalBitmap = createTestBitmap(1024, 768)
        val filename = "integration_test_sticker_${System.currentTimeMillis()}.webp"

        // When: Save the image
        val result = imageRepository.saveImageAsWebP(originalBitmap, filename)

        // Then: Verify the operation was successful
        assertTrue(result.isSuccess, "Image save operation should succeed")
        val savedUri = result.getOrNull()
        assertNotNull(savedUri, "Saved URI should not be null")

        // Verify the saved image properties
        verifyImageProperties(savedUri)

        // Clean up
        originalBitmap.recycle()
    }

    @Test
    fun saveImageAsWebP_preservesWebPFormatAndQuality() = runTest {
        // Given: Create a colorful test bitmap
        val originalBitmap = createColorfulTestBitmap(800, 600)
        val filename = "quality_test_sticker_${System.currentTimeMillis()}.webp"

        // When: Save the image
        val result = imageRepository.saveImageAsWebP(originalBitmap, filename)

        // Then: Verify successful save
        assertTrue(result.isSuccess)
        val savedUri = result.getOrNull()
        assertNotNull(savedUri)

        // Verify the image can be loaded back and has correct format
        verifyWebPFormat(savedUri)

        // Clean up
        originalBitmap.recycle()
    }

    @Test
    fun saveImageAsWebP_handlesSquareImageCorrectly() = runTest {
        // Given: Create a square bitmap (already 512x512)
        val squareBitmap = createTestBitmap(512, 512)
        val filename = "square_test_sticker_${System.currentTimeMillis()}.webp"

        // When: Save the image
        val result = imageRepository.saveImageAsWebP(squareBitmap, filename)

        // Then: Verify successful save
        assertTrue(result.isSuccess)
        val savedUri = result.getOrNull()
        assertNotNull(savedUri)

        // Verify dimensions are maintained
        verifyImageProperties(savedUri)

        // Clean up
        squareBitmap.recycle()
    }

    @Test
    fun saveImageAsWebP_handlesVeryLargeImageCorrectly() = runTest {
        // Given: Create a very large bitmap
        val largeBitmap = createTestBitmap(4096, 3072)
        val filename = "large_test_sticker_${System.currentTimeMillis()}.webp"

        // When: Save the image
        val result = imageRepository.saveImageAsWebP(largeBitmap, filename)

        // Then: Verify successful save and correct resizing
        assertTrue(result.isSuccess)
        val savedUri = result.getOrNull()
        assertNotNull(savedUri)

        // Verify the large image was correctly resized to 512x512
        verifyImageProperties(savedUri)

        // Clean up
        largeBitmap.recycle()
    }

    private fun createTestBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Fill with a simple pattern for testing
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val x = i % width
            val y = i / width
            // Create a checkerboard pattern
            pixels[i] = if ((x + y) % 2 == 0) 0xFF0000FF.toInt() else 0xFFFF0000.toInt()
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        
        return bitmap
    }

    private fun createColorfulTestBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Fill with gradient colors for better quality testing
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val x = i % width
            val y = i / width
            val red = (255 * x / width)
            val green = (255 * y / height)
            val blue = (255 * (x + y) / (width + height))
            pixels[i] = (0xFF000000.toInt()) or (red shl 16) or (green shl 8) or blue
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        
        return bitmap
    }

    private fun verifyImageProperties(uri: Uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Read the image back to verify dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                
                // Reset input stream
                val bytes = inputStream.readBytes()
                val byteInputStream = ByteArrayInputStream(bytes)
                
                BitmapFactory.decodeStream(byteInputStream, null, options)
                
                // Verify dimensions
                assertEquals(512, options.outWidth, "Image width should be 512 pixels")
                assertEquals(512, options.outHeight, "Image height should be 512 pixels")
                
                // Verify MIME type is WebP
                val mimeType = options.outMimeType
                assertTrue(
                    mimeType == "image/webp" || mimeType == "image/x-webp",
                    "Image should be in WebP format, but was: $mimeType"
                )
                
                // Verify the image is not empty
                assertTrue(bytes.isNotEmpty(), "Image file should not be empty")
                
                // Basic WebP signature check (WebP files start with "RIFF" and contain "WEBP")
                val header = String(bytes.take(12).toByteArray())
                assertTrue(header.contains("RIFF") && header.contains("WEBP"), 
                    "File should have valid WebP signature")
            }
        } catch (e: Exception) {
            throw AssertionError("Failed to verify image properties: ${e.message}", e)
        }
    }

    private fun verifyWebPFormat(uri: Uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                assertNotNull(bitmap, "Should be able to decode the saved WebP image")
                
                // Verify dimensions
                assertEquals(512, bitmap.width, "Decoded bitmap width should be 512")
                assertEquals(512, bitmap.height, "Decoded bitmap height should be 512")
                
                // Test that we can compress it again as WebP (format compatibility)
                val outputStream = ByteArrayOutputStream()
                val compressionSuccess = bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
                assertTrue(compressionSuccess, "Should be able to re-compress as WebP")
                
                val compressedData = outputStream.toByteArray()
                assertTrue(compressedData.isNotEmpty(), "Compressed data should not be empty")
                
                bitmap.recycle()
            }
        } catch (e: Exception) {
            throw AssertionError("Failed to verify WebP format: ${e.message}", e)
        }
    }
}
