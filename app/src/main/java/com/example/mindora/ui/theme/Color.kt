package com.example.mindora.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class MindoraPalette(
    val deepMidnightPurple: Color,
    val richViolet: Color,
    val electricLavender: Color,
    val softCream: Color,
    val coralAccent: Color,
    val limeAccent: Color,
    val appSurface: Color,
    val surfaceBorder: Color,
    val surfaceDarkBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val successGreen: Color,
    val successGreenLight: Color,
    val warningOrange: Color,
    val warningOrangeLight: Color,
    val errorRed: Color,
    val errorRedLight: Color,
    val primaryPurpleLight: Color
)

val MindoraLightPalette = MindoraPalette(
    deepMidnightPurple = Color(0xFF211B3A),
    richViolet = Color(0xFF7965E8),
    electricLavender = Color(0xFFB7A8FF),
    softCream = Color(0xFFF8F6F0),
    coralAccent = Color(0xFFFF806B),
    limeAccent = Color(0xFFC5E89B),
    appSurface = Color(0xF5FFFFFF), // Slight 96% alpha translucency so Pattern-Craft grids/dots peek through subtly
    surfaceBorder = Color(0xFFE4DFEE),
    surfaceDarkBorder = Color(0xFF332B54),
    textPrimary = Color(0xFF211B3A),
    textSecondary = Color(0xFF625D70),
    textMuted = Color(0xFF716B7E),
    successGreen = Color(0xFF27865B),
    successGreenLight = Color(0xFFEBF7F1),
    warningOrange = Color(0xFFAD681C),
    warningOrangeLight = Color(0xFFFFF6EC),
    errorRed = Color(0xFFB83C4D),
    errorRedLight = Color(0xFFFDF0F0),
    primaryPurpleLight = Color(0xFFF0ECFE)
)

val MindoraDarkPalette = MindoraPalette(
    deepMidnightPurple = Color(0xFF29243A),
    richViolet = Color(0xFFB4A5FF),
    electricLavender = Color(0xFFD0C6FF),
    softCream = Color(0xFF15131B),
    coralAccent = Color(0xFFFF907C),
    limeAccent = Color(0xFFD0F49F),
    appSurface = Color(0xF2211E2A), // 95% alpha translucency for dark pattern peek-through
    surfaceBorder = Color(0xFF393445),
    surfaceDarkBorder = Color(0xFF393445),
    textPrimary = Color(0xFFF4F0FA),
    textSecondary = Color(0xFFC5BED3),
    textMuted = Color(0xFF9A92AA),
    successGreen = Color(0xFF75D7A6),
    successGreenLight = Color(0xFF1F382E),
    warningOrange = Color(0xFFFFC078),
    warningOrangeLight = Color(0xFF3C2D1E),
    errorRed = Color(0xFFFF8A97),
    errorRedLight = Color(0xFF3B242B),
    primaryPurpleLight = Color(0xFF322B47)
)

val LocalMindoraPalette = staticCompositionLocalOf { MindoraLightPalette }

private val Palette: MindoraPalette
    @Composable get() = LocalMindoraPalette.current

val DeepMidnightPurple: Color @Composable get() = Palette.deepMidnightPurple
val RichViolet: Color @Composable get() = Palette.richViolet
val ElectricLavender: Color @Composable get() = Palette.electricLavender
val SoftCream: Color @Composable get() = Palette.softCream
val CoralAccent: Color @Composable get() = Palette.coralAccent
val LimeAccent: Color @Composable get() = Palette.limeAccent

val AppSurface: Color @Composable get() = Palette.appSurface
val SurfaceBorder: Color @Composable get() = Palette.surfaceBorder
val SurfaceDarkBorder: Color @Composable get() = Palette.surfaceDarkBorder

val TextPrimary: Color @Composable get() = Palette.textPrimary
val TextSecondary: Color @Composable get() = Palette.textSecondary
val TextMuted: Color @Composable get() = Palette.textMuted

val SuccessGreen: Color @Composable get() = Palette.successGreen
val SuccessGreenLight: Color @Composable get() = Palette.successGreenLight
val WarningOrange: Color @Composable get() = Palette.warningOrange
val WarningOrangeLight: Color @Composable get() = Palette.warningOrangeLight
val ErrorRed: Color @Composable get() = Palette.errorRed
val ErrorRedLight: Color @Composable get() = Palette.errorRedLight

val PrimaryPurple: Color @Composable get() = RichViolet
val DeepPurple: Color @Composable get() = DeepMidnightPurple
val SoftLavender: Color @Composable get() = ElectricLavender
val PrimaryPurpleLight: Color @Composable get() = Palette.primaryPurpleLight
val PrimaryPurpleDark: Color @Composable get() = DeepMidnightPurple
val SecondaryLavender: Color @Composable get() = RichViolet
val SecondaryLavenderLight: Color @Composable get() = ElectricLavender
val AppBackground: Color @Composable get() = SoftCream
val WarningYellow: Color @Composable get() = WarningOrange
val WarningYellowLight: Color @Composable get() = WarningOrangeLight

val SubjectColorPresets = listOf(
    "#7965E8",
    "#FF806B",
    "#55B889",
    "#F3AE62",
    "#3A86EF",
    "#EC4899",
    "#8B5CF6",
    "#211B3A"
)
