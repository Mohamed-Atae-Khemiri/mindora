package com.example.mindora.ui.theme

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

enum class BackgroundMode {
    PRESET,
    CUSTOM_PHOTO
}

@Composable
fun AppBackgroundContainer(
    mode: BackgroundMode,
    presetId: String,
    customPhotoUri: String?,
    dimLevel: Float,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val customBitmap = remember(customPhotoUri) {
        if (!customPhotoUri.isNullOrBlank() && customPhotoUri != "null") {
            loadBitmapFromUri(context, customPhotoUri)
        } else null
    }

    val activePreset = remember(presetId) {
        getPatternCraftTheme(presetId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            mode == BackgroundMode.CUSTOM_PHOTO && customBitmap != null -> {
                Image(
                    bitmap = customBitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Scrim overlay to ensure contrast and readability over any photo
                val scrimColor = if (isDarkMode) Color.Black else Color(0xFF211B3A)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(scrimColor.copy(alpha = dimLevel.coerceIn(0.1f, 0.85f)))
                )
            }
            else -> {
                // High-performance Pattern-Craft drawing with cache
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .patternCraftBackground(activePreset)
                )
            }
        }
        content()
    }
}

private fun loadBitmapFromUri(context: Context, uriString: String): ImageBitmap? {
    return runCatching {
        val uri = Uri.parse(uriString)
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    }.getOrNull()
}
