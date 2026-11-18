package com.example.mindora.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.ui.theme.ElectricLavender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StudentAvatar(
    profile: StudentProfile?,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    avatarUriOverride: String? = profile?.avatarUri
) {
    val context = LocalContext.current
    val uri = avatarUriOverride?.let(Uri::parse)
    val bitmap = produceState<Bitmap?>(null, uri) {
        value = if (uri == null) null else withContext(Dispatchers.IO) {
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }
                var sample = 1
                while (bounds.outWidth / sample > 512 || bounds.outHeight / sample > 512) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize = sample }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }.getOrNull()
        }
    }.value
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ElectricLavender)
            .semantics {
                contentDescription = profile?.fullName?.let { "Profile photo for $it" } ?: "Panda Study mascot"
            },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.matchParentSize())
        } else {
            Image(painterResource(com.example.mindora.R.drawable.panda_study), null, modifier = Modifier.matchParentSize())
        }
    }
}
