package com.stickercreator.app.ui.screens.crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stickercreator.app.data.repository.ImageRepository
import com.stickercreator.app.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

data class CropUiState(
    val bitmap: Bitmap? = null,
    val cropRect: Rect = Rect.Zero,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class CropViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CropUiState())
    val uiState: StateFlow<CropUiState> = _uiState.asStateFlow()

    fun loadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Validate URI using ImageUtils
                if (uri == Uri.EMPTY || uri.toString().isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Invalid image URI"
                    )
                    return@launch
                }

                // Check if we can access the image
                if (!ImageUtils.canAccessImage(context, uri)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Unable to access image. Please check permissions."
                    )
                    return@launch
                }

                // Safely load the bitmap with memory management
                val bitmap = ImageUtils.safeLoadBitmap(context, uri)

                if (ImageUtils.isValidBitmap(bitmap)) {
                    val initialCropRect = calculateInitialCropRect(bitmap!!)
                    _uiState.value = _uiState.value.copy(
                        bitmap = bitmap,
                        cropRect = initialCropRect,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Failed to decode image. The file may be corrupted or unsupported."
                    )
                }
            } catch (e: SecurityException) {
                android.util.Log.w("CropViewModel", "Permission denied: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Permission denied. Please grant storage permission."
                )
            } catch (e: OutOfMemoryError) {
                android.util.Log.w("CropViewModel", "OutOfMemoryError: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Image too large. Please select a smaller image."
                )
            } catch (e: Exception) {
                android.util.Log.e("CropViewModel", "Error loading image: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error loading image: ${e.message}"
                )
            }
        }
    }

    private fun calculateInitialCropRect(bitmap: Bitmap): Rect {
        val size = min(bitmap.width, bitmap.height).toFloat()
        val centerX = bitmap.width / 2f
        val centerY = bitmap.height / 2f
        val halfSize = size / 2f

        return Rect(
            left = centerX - halfSize,
            top = centerY - halfSize,
            right = centerX + halfSize,
            bottom = centerY + halfSize
        )
    }

    fun updateCropRect(newRect: Rect) {
        _uiState.value = _uiState.value.copy(cropRect = newRect)
    }

    fun resetCrop() {
        _uiState.value.bitmap?.let { bitmap ->
            val initialCropRect = calculateInitialCropRect(bitmap)
            _uiState.value = _uiState.value.copy(cropRect = initialCropRect)
        }
    }

    fun saveCroppedImage() {
        val bitmap = _uiState.value.bitmap ?: return
        val cropRect = _uiState.value.cropRect

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                // Create cropped bitmap
                val croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    cropRect.left.toInt().coerceAtLeast(0),
                    cropRect.top.toInt().coerceAtLeast(0),
                    (cropRect.width.toInt()).coerceAtMost(bitmap.width - cropRect.left.toInt()),
                    (cropRect.height.toInt()).coerceAtMost(bitmap.height - cropRect.top.toInt())
                )

                // Save as WebP
                val result = imageRepository.saveImageAsWebP(croppedBitmap)

                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            isSaved = true,
                            message = "Sticker saved successfully!"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            message = "Failed to save sticker: ${error.message}"
                        )
                    }
                )

                // Clean up cropped bitmap
                if (croppedBitmap != bitmap) {
                    croppedBitmap.recycle()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Error saving image: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isSaving = false,
            message = message
        )
    }
}
