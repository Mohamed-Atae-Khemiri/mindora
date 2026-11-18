package com.example.mindora.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Composable
fun MindoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) MindoraDarkPalette else MindoraLightPalette
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.richViolet,
            onPrimary = palette.deepMidnightPurple,
            primaryContainer = palette.primaryPurpleLight,
            onPrimaryContainer = palette.textPrimary,
            secondary = palette.electricLavender,
            onSecondary = palette.deepMidnightPurple,
            background = palette.softCream,
            onBackground = palette.textPrimary,
            surface = palette.appSurface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.appSurface,
            onSurfaceVariant = palette.textSecondary,
            error = palette.errorRed,
            onError = palette.deepMidnightPurple,
            errorContainer = palette.errorRedLight,
            onErrorContainer = palette.errorRed
        )
    } else {
        lightColorScheme(
            primary = palette.richViolet,
            onPrimary = Color.White,
            primaryContainer = palette.primaryPurpleLight,
            onPrimaryContainer = palette.deepMidnightPurple,
            secondary = palette.electricLavender,
            onSecondary = palette.deepMidnightPurple,
            background = palette.softCream,
            onBackground = palette.textPrimary,
            surface = palette.appSurface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.softCream,
            onSurfaceVariant = palette.textSecondary,
            error = palette.errorRed,
            onError = Color.White,
            errorContainer = palette.errorRedLight,
            onErrorContainer = palette.errorRed
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalMindoraPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = mindoraTypography(),
            shapes = Shapes(
                extraSmall = RoundedCornerShape(10.dp),
                small = RoundedCornerShape(14.dp),
                medium = RoundedCornerShape(20.dp),
                large = RoundedCornerShape(28.dp),
                extraLarge = RoundedCornerShape(32.dp)
            ),
            content = content
        )
    }
}
