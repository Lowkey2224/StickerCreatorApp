package com.stickercreator.app.ui.screens.crop

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.stickercreator.app.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val HANDLE_SIZE = 20f
private const val MIN_CROP_SIZE = 50f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(
    imageUri: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CropViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(imageUri) {
        viewModel.loadImage(context, Uri.parse(imageUri))
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crop_image)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.bitmap != null) {
                        IconButton(
                            onClick = { viewModel.resetCrop() }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.bitmap != null -> {
                    // Image with crop overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CropImageView(
                            bitmap = uiState.bitmap!!,
                            cropRect = uiState.cropRect,
                            onCropRectChanged = viewModel::updateCropRect,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Bottom controls
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.crop_area_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.resetCrop() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.reset_crop))
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = { viewModel.saveCroppedImage() },
                                enabled = !uiState.isSaving,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Save,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.save_sticker))
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_image_selected),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CropImageView(
    bitmap: Bitmap,
    cropRect: Rect,
    onCropRectChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clipToBounds()) {
        // Display the image
        AsyncImage(
            model = bitmap,
            contentDescription = "Image to crop",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        // Crop overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                        val newRect = updateCropRect(cropRect, change.position, dragAmount, canvasSize)
                        onCropRectChanged(newRect)
                    }
                }
        ) {
            drawCropOverlay(cropRect, size)
        }
    }
}

private fun updateCropRect(
    currentRect: Rect,
    touchPoint: Offset,
    dragAmount: Offset,
    canvasSize: androidx.compose.ui.geometry.Size
): Rect {
    val handleSize = HANDLE_SIZE
    val left = currentRect.left
    val top = currentRect.top
    val right = currentRect.right
    val bottom = currentRect.bottom

    // Determine which handle or area is being dragged
    return when {
        // Top-left corner
        abs(touchPoint.x - left) < handleSize && abs(touchPoint.y - top) < handleSize -> {
            val newLeft = max(0f, min(left + dragAmount.x, right - MIN_CROP_SIZE))
            val newTop = max(0f, min(top + dragAmount.y, bottom - MIN_CROP_SIZE))
            Rect(newLeft, newTop, right, bottom)
        }
        // Top-right corner
        abs(touchPoint.x - right) < handleSize && abs(touchPoint.y - top) < handleSize -> {
            val newRight = min(canvasSize.width, max(right + dragAmount.x, left + MIN_CROP_SIZE))
            val newTop = max(0f, min(top + dragAmount.y, bottom - MIN_CROP_SIZE))
            Rect(left, newTop, newRight, bottom)
        }
        // Bottom-left corner
        abs(touchPoint.x - left) < handleSize && abs(touchPoint.y - bottom) < handleSize -> {
            val newLeft = max(0f, min(left + dragAmount.x, right - MIN_CROP_SIZE))
            val newBottom = min(canvasSize.height, max(bottom + dragAmount.y, top + MIN_CROP_SIZE))
            Rect(newLeft, top, right, newBottom)
        }
        // Bottom-right corner
        abs(touchPoint.x - right) < handleSize && abs(touchPoint.y - bottom) < handleSize -> {
            val newRight = min(canvasSize.width, max(right + dragAmount.x, left + MIN_CROP_SIZE))
            val newBottom = min(canvasSize.height, max(bottom + dragAmount.y, top + MIN_CROP_SIZE))
            Rect(left, top, newRight, newBottom)
        }
        // Inside the rectangle - move entire rectangle
        touchPoint.x > left && touchPoint.x < right && touchPoint.y > top && touchPoint.y < bottom -> {
            val width = right - left
            val height = bottom - top
            val newLeft = max(0f, min(left + dragAmount.x, canvasSize.width - width))
            val newTop = max(0f, min(top + dragAmount.y, canvasSize.height - height))
            Rect(newLeft, newTop, newLeft + width, newTop + height)
        }
        else -> currentRect
    }
}

private fun DrawScope.drawCropOverlay(cropRect: Rect, canvasSize: Size) {
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val cropBorderColor = Color.White
    val handleColor = Color.White
    val handleSize = HANDLE_SIZE

    // Draw overlay (darken areas outside crop)
    // Top
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, 0f),
        size = Size(canvasSize.width, cropRect.top)
    )
    // Bottom
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.bottom),
        size = Size(canvasSize.width, canvasSize.height - cropRect.bottom)
    )
    // Left
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.top),
        size = Size(cropRect.left, cropRect.height)
    )
    // Right
    drawRect(
        color = overlayColor,
        topLeft = Offset(cropRect.right, cropRect.top),
        size = Size(canvasSize.width - cropRect.right, cropRect.height)
    )

    // Draw crop rectangle border
    drawRect(
        color = cropBorderColor,
        topLeft = cropRect.topLeft,
        size = cropRect.size,
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw corner handles
    val handleOffset = handleSize / 2
    listOf(
        cropRect.topLeft,
        Offset(cropRect.right, cropRect.top),
        cropRect.bottomRight,
        Offset(cropRect.left, cropRect.bottom)
    ).forEach { corner ->
        drawRect(
            color = handleColor,
            topLeft = Offset(corner.x - handleOffset, corner.y - handleOffset),
            size = Size(handleSize, handleSize)
        )
    }
}
