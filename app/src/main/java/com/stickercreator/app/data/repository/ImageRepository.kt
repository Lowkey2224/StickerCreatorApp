package com.stickercreator.app.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun saveImageAsWebP(
        bitmap: Bitmap,
        filename: String = "sticker_${System.currentTimeMillis()}.webp"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val resizedBitmap = resizeBitmapTo512x512(bitmap)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageToMediaStore(resizedBitmap, filename)
            } else {
                saveImageToExternalStorage(resizedBitmap, filename)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun resizeBitmapTo512x512(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 512, 512, true)
    }
    
    private fun saveImageToMediaStore(bitmap: Bitmap, filename: String): Result<Uri> {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/webp")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/StickerCreator")
        }
        
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return Result.failure(Exception("Failed to create MediaStore entry"))
        
        return try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            }
            Result.success(uri)
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            Result.failure(e)
        }
    }
    
    private fun saveImageToExternalStorage(bitmap: Bitmap, filename: String): Result<Uri> {
        val picturesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "StickerCreator"
        )
        
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }
        
        val file = File(picturesDir, filename)
        
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            }
            
            // Notify media scanner
            val uri = Uri.fromFile(file)
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                filename,
                "Sticker created with Sticker Creator"
            )
            
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
