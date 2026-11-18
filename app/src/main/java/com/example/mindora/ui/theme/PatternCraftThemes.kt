package com.example.mindora.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PatternCategory(val label: String) {
    ALL("All"),
    GRADIENTS("Gradients"),
    GEOMETRIC_GRIDS("Grids"),
    DOT_MATRICES("Dot Matrix"),
    DARK_AMBIENT("Dark / OLED")
}

sealed interface PatternStyle {
    object Solid : PatternStyle

    data class LinearGradient(
        val colors: List<Color>,
        val startYFactor: Float = 0f,
        val endYFactor: Float = 1f
    ) : PatternStyle

    data class RadialGlow(
        val colors: List<Color>,
        val centerRatio: Offset = Offset(0.5f, 0f),
        val radiusRatio: Float = 1.0f
    ) : PatternStyle

    data class Grid(
        val spacingDp: Dp = 28.dp,
        val lineColor: Color,
        val strokeWidthDp: Dp = 1.dp,
        val glowColors: List<Color>? = null,
        val glowCenterRatio: Offset = Offset(0.8f, 0.2f),
        val glowRadiusRatio: Float = 0.8f,
        val isDiagonal: Boolean = false
    ) : PatternStyle

    data class DotMatrix(
        val spacingDp: Dp = 24.dp,
        val dotRadiusDp: Dp = 1.5.dp,
        val dotColor: Color,
        val glowColors: List<Color>? = null,
        val glowCenterRatio: Offset = Offset(0.5f, 0.15f),
        val glowRadiusRatio: Float = 0.75f
    ) : PatternStyle

    data class DualSpotlightGrid(
        val spacingDp: Dp = 28.dp,
        val lineColor: Color,
        val strokeWidthDp: Dp = 1.dp,
        val spotlight1Colors: List<Color>,
        val spotlight1Center: Offset = Offset(0.15f, 0.15f),
        val spotlight2Colors: List<Color>,
        val spotlight2Center: Offset = Offset(0.85f, 0.85f),
        val radiusRatio: Float = 0.7f
    ) : PatternStyle
}

data class BackgroundPreset(
    val id: String,
    val name: String,
    val backgroundColor: Color,
    val isDark: Boolean = false,
    val category: PatternCategory = PatternCategory.GRADIENTS,
    val patternStyle: PatternStyle = PatternStyle.Solid,
    val gradientColors: List<Color>? = null
) {
    val brush: Brush?
        get() = gradientColors?.let { Brush.verticalGradient(it) }
}

/**
 * Curated registry of Pattern-Craft background themes adapted for Jetpack Compose.
 * Preserves legacy IDs for 100% backward compatibility with stored user preferences.
 */
val AppBackgroundPresets: List<BackgroundPreset> = listOf(
    // ---- Gradients & Radial Glows ----
    BackgroundPreset(
        id = "classic_cream",
        name = "Classic Cream",
        backgroundColor = Color(0xFFF8F6F0),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.LinearGradient(
            colors = listOf(Color(0xFFFAF8F3), Color(0xFFF5F2EB), Color(0xFFF8F6F0))
        ),
        gradientColors = listOf(Color(0xFFFAF8F3), Color(0xFFF5F2EB), Color(0xFFF8F6F0))
    ),
    BackgroundPreset(
        id = "sunset_lavender",
        name = "Sunset Lavender",
        backgroundColor = Color(0xFFF2EEFD),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.LinearGradient(
            colors = listOf(Color(0xFFF3EAFF), Color(0xFFFDF0ED), Color(0xFFF8F6F0))
        ),
        gradientColors = listOf(Color(0xFFF3EAFF), Color(0xFFFDF0ED), Color(0xFFF8F6F0))
    ),
    BackgroundPreset(
        id = "beach_sunset",
        name = "Beach Sunset",
        backgroundColor = Color(0xFFE7DFD3),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.LinearGradient(
            colors = listOf(
                Color(0xFFCDEBFA),
                Color(0xFFFFF4E0),
                Color(0xFFFFE2C2),
                Color(0xFFFFC68E),
                Color(0xFFF59173),
                Color(0xFFBE5B5B)
            )
        ),
        gradientColors = listOf(
            Color(0xFFCDEBFA),
            Color(0xFFFFF4E0),
            Color(0xFFFFE2C2),
            Color(0xFFFFC68E),
            Color(0xFFF59173),
            Color(0xFFBE5B5B)
        )
    ),
    BackgroundPreset(
        id = "top_teal_glow",
        name = "Top Teal Glow",
        backgroundColor = Color(0xFFF4FAF9),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.RadialGlow(
            colors = listOf(Color(0x5514B8A6), Color(0x152DD4BF), Color.Transparent),
            centerRatio = Offset(0.5f, 0.05f),
            radiusRatio = 0.95f
        ),
        gradientColors = listOf(Color(0xFFE6F8F5), Color(0xFFF4FAF9))
    ),
    BackgroundPreset(
        id = "top_violet_radial",
        name = "Violet Horizon",
        backgroundColor = Color(0xFFF8F5FF),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.RadialGlow(
            colors = listOf(Color(0x457C3AED), Color(0x15A78BFA), Color.Transparent),
            centerRatio = Offset(0.5f, 0.05f),
            radiusRatio = 1.0f
        ),
        gradientColors = listOf(Color(0xFFF3EAFF), Color(0xFFF8F5FF))
    ),
    BackgroundPreset(
        id = "sakura_bloom",
        name = "Sakura Rose",
        backgroundColor = Color(0xFFFDF2F8),
        isDark = false,
        category = PatternCategory.GRADIENTS,
        patternStyle = PatternStyle.LinearGradient(
            colors = listOf(Color(0xFFFCE7F3), Color(0xFFFFF1F2), Color(0xFFF8F6F0))
        ),
        gradientColors = listOf(Color(0xFFFCE7F3), Color(0xFFFFF1F2), Color(0xFFF8F6F0))
    ),

    // ---- Geometric Grids ----
    BackgroundPreset(
        id = "purple_grid_spotlight",
        name = "Purple Spotlight Grid",
        backgroundColor = Color(0xFFFAF9FF),
        isDark = false,
        category = PatternCategory.GEOMETRIC_GRIDS,
        patternStyle = PatternStyle.Grid(
            spacingDp = 28.dp,
            lineColor = Color(0xFFE8E5F5),
            strokeWidthDp = 1.dp,
            glowColors = listOf(Color(0x55DDD6FE), Color(0x18C4B5FD), Color.Transparent),
            glowCenterRatio = Offset(0.85f, 0.15f),
            glowRadiusRatio = 0.9f
        ),
        gradientColors = listOf(Color(0xFFF5F2FF), Color(0xFFFAF9FF))
    ),
    BackgroundPreset(
        id = "dual_gradient_grid",
        name = "Dual Spotlight Grid",
        backgroundColor = Color(0xFFFAFAFD),
        isDark = false,
        category = PatternCategory.GEOMETRIC_GRIDS,
        patternStyle = PatternStyle.DualSpotlightGrid(
            spacingDp = 28.dp,
            lineColor = Color(0x80E5E7EB),
            strokeWidthDp = 1.dp,
            spotlight1Colors = listOf(Color(0x50C4B5FD), Color.Transparent),
            spotlight1Center = Offset(0.15f, 0.15f),
            spotlight2Colors = listOf(Color(0x4593C5FD), Color.Transparent),
            spotlight2Center = Offset(0.85f, 0.85f),
            radiusRatio = 0.75f
        ),
        gradientColors = listOf(Color(0xFFF3EAFF), Color(0xFFEFF6FF))
    ),
    BackgroundPreset(
        id = "classic_blueprint",
        name = "Classic Blueprint",
        backgroundColor = Color(0xFFF0F6FF),
        isDark = false,
        category = PatternCategory.GEOMETRIC_GRIDS,
        patternStyle = PatternStyle.Grid(
            spacingDp = 24.dp,
            lineColor = Color(0xFFDBEAFE),
            strokeWidthDp = 1.dp
        ),
        gradientColors = listOf(Color(0xFFDBEAFE), Color(0xFFF0F6FF))
    ),
    BackgroundPreset(
        id = "diagonal_cross_grid",
        name = "Diagonal Cross Grid",
        backgroundColor = Color(0xFFF9FAFB),
        isDark = false,
        category = PatternCategory.GEOMETRIC_GRIDS,
        patternStyle = PatternStyle.Grid(
            spacingDp = 30.dp,
            lineColor = Color(0x60D1D5DB),
            strokeWidthDp = 1.dp,
            isDiagonal = true
        ),
        gradientColors = listOf(Color(0xFFF3F4F6), Color(0xFFF9FAFB))
    ),

    // ---- Dot Matrices ----
    BackgroundPreset(
        id = "minimal_dots_cream",
        name = "Minimal Dots Cream",
        backgroundColor = Color(0xFFF8F6F0),
        isDark = false,
        category = PatternCategory.DOT_MATRICES,
        patternStyle = PatternStyle.DotMatrix(
            spacingDp = 24.dp,
            dotRadiusDp = 1.5.dp,
            dotColor = Color(0xFFD6CEC5)
        ),
        gradientColors = listOf(Color(0xFFF8F6F0), Color(0xFFF5F2EB))
    ),
    BackgroundPreset(
        id = "ocean_breeze",
        name = "Ocean Dot Matrix",
        backgroundColor = Color(0xFFEFF6FF),
        isDark = false,
        category = PatternCategory.DOT_MATRICES,
        patternStyle = PatternStyle.DotMatrix(
            spacingDp = 24.dp,
            dotRadiusDp = 1.5.dp,
            dotColor = Color(0xFF93C5FD),
            glowColors = listOf(Color(0x40BAE6FD), Color.Transparent),
            glowCenterRatio = Offset(0.5f, 0.1f),
            glowRadiusRatio = 0.8f
        ),
        gradientColors = listOf(Color(0xFFDBEAFE), Color(0xFFEFF6FF), Color(0xFFF8F6F0))
    ),
    BackgroundPreset(
        id = "emerald_focus",
        name = "Emerald Study Dots",
        backgroundColor = Color(0xFFF0F9F4),
        isDark = false,
        category = PatternCategory.DOT_MATRICES,
        patternStyle = PatternStyle.DotMatrix(
            spacingDp = 24.dp,
            dotRadiusDp = 1.5.dp,
            dotColor = Color(0xFFA7D7B9),
            glowColors = listOf(Color(0x35D1FAE5), Color.Transparent),
            glowCenterRatio = Offset(0.5f, 0.15f),
            glowRadiusRatio = 0.85f
        ),
        gradientColors = listOf(Color(0xFFE4F5EB), Color(0xFFF2FAF5), Color(0xFFF8F6F0))
    ),

    // ---- Dark & OLED Ambient ----
    BackgroundPreset(
        id = "cosmic_midnight",
        name = "Cosmic Midnight",
        backgroundColor = Color(0xFF15131B),
        isDark = true,
        category = PatternCategory.DARK_AMBIENT,
        patternStyle = PatternStyle.LinearGradient(
            colors = listOf(Color(0xFF241E38), Color(0xFF191624), Color(0xFF15131B))
        ),
        gradientColors = listOf(Color(0xFF241E38), Color(0xFF191624), Color(0xFF15131B))
    ),
    BackgroundPreset(
        id = "dark_dot_matrix",
        name = "Dark Dot Matrix",
        backgroundColor = Color(0xFF0D0C13),
        isDark = true,
        category = PatternCategory.DARK_AMBIENT,
        patternStyle = PatternStyle.DotMatrix(
            spacingDp = 22.dp,
            dotRadiusDp = 1.5.dp,
            dotColor = Color(0xFF38324C),
            glowColors = listOf(Color(0x357C3AED), Color(0x10A78BFA), Color.Transparent),
            glowCenterRatio = Offset(0.5f, 0.1f),
            glowRadiusRatio = 0.8f
        ),
        gradientColors = listOf(Color(0xFF1C172B), Color(0xFF0D0C13))
    ),
    BackgroundPreset(
        id = "cyber_slate_grid",
        name = "Cyber Slate Grid",
        backgroundColor = Color(0xFF0F172A),
        isDark = true,
        category = PatternCategory.DARK_AMBIENT,
        patternStyle = PatternStyle.Grid(
            spacingDp = 28.dp,
            lineColor = Color(0xFF1E293B),
            strokeWidthDp = 1.dp,
            glowColors = listOf(Color(0x400E7490), Color.Transparent),
            glowCenterRatio = Offset(0.85f, 0.15f),
            glowRadiusRatio = 0.85f
        ),
        gradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
    ),
    BackgroundPreset(
        id = "dark_horizon_glow",
        name = "Dark Horizon Glow",
        backgroundColor = Color(0xFF0A0910),
        isDark = true,
        category = PatternCategory.DARK_AMBIENT,
        patternStyle = PatternStyle.RadialGlow(
            colors = listOf(Color(0x554C1D95), Color(0x181E1035), Color.Transparent),
            centerRatio = Offset(0.5f, 0.95f),
            radiusRatio = 0.9f
        ),
        gradientColors = listOf(Color(0xFF0A0910), Color(0xFF2E1065))
    )
)

/**
 * Safely resolves a preset by ID, falling back to Classic Cream if not found.
 */
fun getPatternCraftTheme(presetId: String?): BackgroundPreset {
    return AppBackgroundPresets.firstOrNull { it.id == presetId } ?: AppBackgroundPresets.first()
}

/**
 * High-performance cached drawing modifier using [Modifier.drawWithCache].
 * Ensures Brushes, Paths, dot grids, and spacing measurements are cached and only recalculated
 * when component dimensions change, running at zero allocation cost during animation and scroll.
 */
fun Modifier.patternCraftBackground(preset: BackgroundPreset): Modifier = this.drawWithCache {
    val width = size.width
    val height = size.height
    val maxDim = maxOf(width, height)

    when (val style = preset.patternStyle) {
        is PatternStyle.Solid -> {
            val bg = preset.backgroundColor
            onDrawBehind {
                drawRect(color = bg)
            }
        }

        is PatternStyle.LinearGradient -> {
            val brush = Brush.verticalGradient(
                colors = style.colors,
                startY = height * style.startYFactor,
                endY = height * style.endYFactor
            )
            onDrawBehind {
                drawRect(brush = brush)
            }
        }

        is PatternStyle.RadialGlow -> {
            val bg = preset.backgroundColor
            val center = Offset(width * style.centerRatio.x, height * style.centerRatio.y)
            val radius = maxDim * style.radiusRatio
            val radialBrush = Brush.radialGradient(
                colors = style.colors,
                center = center,
                radius = radius.coerceAtLeast(1f)
            )
            onDrawBehind {
                drawRect(color = bg)
                drawRect(brush = radialBrush)
            }
        }

        is PatternStyle.Grid -> {
            val bg = preset.backgroundColor
            val stepPx = style.spacingDp.toPx().coerceAtLeast(8f)
            val strokePx = style.strokeWidthDp.toPx().coerceAtLeast(0.5f)
            val lineColor = style.lineColor
            val glowBrush = style.glowColors?.let { colors ->
                val center = Offset(width * style.glowCenterRatio.x, height * style.glowCenterRatio.y)
                Brush.radialGradient(
                    colors = colors,
                    center = center,
                    radius = (maxDim * style.glowRadiusRatio).coerceAtLeast(1f)
                )
            }

            if (style.isDiagonal) {
                onDrawBehind {
                    drawRect(color = bg)
                    glowBrush?.let { drawRect(brush = it) }

                    var offset = -height
                    while (offset <= width + height) {
                        drawLine(
                            color = lineColor,
                            start = Offset(offset, 0f),
                            end = Offset(offset + height, height),
                            strokeWidth = strokePx
                        )
                        drawLine(
                            color = lineColor,
                            start = Offset(offset + height, 0f),
                            end = Offset(offset, height),
                            strokeWidth = strokePx
                        )
                        offset += stepPx
                    }
                }
            } else {
                onDrawBehind {
                    drawRect(color = bg)
                    glowBrush?.let { drawRect(brush = it) }

                    var x = 0f
                    while (x <= width) {
                        drawLine(
                            color = lineColor,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = strokePx
                        )
                        x += stepPx
                    }
                    var y = 0f
                    while (y <= height) {
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = strokePx
                        )
                        y += stepPx
                    }
                }
            }
        }

        is PatternStyle.DotMatrix -> {
            val bg = preset.backgroundColor
            val stepPx = style.spacingDp.toPx().coerceAtLeast(8f)
            val dotRadiusPx = style.dotRadiusDp.toPx().coerceAtLeast(0.5f)
            val dotColor = style.dotColor
            val glowBrush = style.glowColors?.let { colors ->
                val center = Offset(width * style.glowCenterRatio.x, height * style.glowCenterRatio.y)
                Brush.radialGradient(
                    colors = colors,
                    center = center,
                    radius = (maxDim * style.glowRadiusRatio).coerceAtLeast(1f)
                )
            }

            onDrawBehind {
                drawRect(color = bg)
                glowBrush?.let { drawRect(brush = it) }

                var x = stepPx / 2f
                while (x < width) {
                    var y = stepPx / 2f
                    while (y < height) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadiusPx,
                            center = Offset(x, y)
                        )
                        y += stepPx
                    }
                    x += stepPx
                }
            }
        }

        is PatternStyle.DualSpotlightGrid -> {
            val bg = preset.backgroundColor
            val stepPx = style.spacingDp.toPx().coerceAtLeast(8f)
            val strokePx = style.strokeWidthDp.toPx().coerceAtLeast(0.5f)
            val lineColor = style.lineColor
            val spot1 = Brush.radialGradient(
                colors = style.spotlight1Colors,
                center = Offset(width * style.spotlight1Center.x, height * style.spotlight1Center.y),
                radius = (maxDim * style.radiusRatio).coerceAtLeast(1f)
            )
            val spot2 = Brush.radialGradient(
                colors = style.spotlight2Colors,
                center = Offset(width * style.spotlight2Center.x, height * style.spotlight2Center.y),
                radius = (maxDim * style.radiusRatio).coerceAtLeast(1f)
            )

            onDrawBehind {
                drawRect(color = bg)
                drawRect(brush = spot1)
                drawRect(brush = spot2)

                var x = 0f
                while (x <= width) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = strokePx
                    )
                    x += stepPx
                }
                var y = 0f
                while (y <= height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = strokePx
                    )
                    y += stepPx
                }
            }
        }
    }
}
