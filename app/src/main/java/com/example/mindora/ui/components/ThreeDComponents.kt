package com.example.mindora.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SoftCream
import com.example.mindora.ui.theme.SurfaceBorder
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary

// Modern 3D Color Constants
val ThreeDGradientPurple = Brush.linearGradient(
    colors = listOf(Color(0xFF7965E8), Color(0xFF5541D7))
)
val ThreeDGradientLavender = Brush.linearGradient(
    colors = listOf(Color(0xFFD6CEFF), Color(0xFFB8A8FF))
)
val ThreeDGradientDarkPurple = Brush.linearGradient(
    colors = listOf(Color(0xFF2E2452), Color(0xFF1B1438))
)
val ThreeDGradientGlass = Brush.linearGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF3EFFF))
)
val ThreeDGradientCoral = Brush.linearGradient(
    colors = listOf(Color(0xFFFF9583), Color(0xFFFF6B53))
)
val ThreeDGradientMint = Brush.linearGradient(
    colors = listOf(Color(0xFFD5F7AA), Color(0xFFA5E668))
)

/**
 * Premium 3D Floating Glass Card with bevels, ambient inner shadow, specular top highlight,
 * and offset depth layer for high-end visual realism.
 */
@Composable
fun ThreeDGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(26.dp),
    backgroundColor: Color = Color(0xF8FFFFFF),
    depthColor: Color = Color(0x283A2E7D),
    depthOffset: Dp = 6.dp,
    borderColor: Color = Color(0x55B8A8FF),
    glowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedDepth by animateDpAsState(
        targetValue = if (isPressed) 2.dp else depthOffset,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "card3dDepth"
    )

    Box(
        modifier = modifier
            .drawBehind {
                val cornerPx = shape.topStart.toPx(size, this)
                val depthPx = animatedDepth.toPx()

                // Glow ambient backdrop
                if (glowColor != null) {
                    drawRoundRect(
                        color = glowColor.copy(alpha = 0.22f),
                        topLeft = Offset(-4.dp.toPx(), -2.dp.toPx() + depthPx * 0.4f),
                        size = Size(size.width + 8.dp.toPx(), size.height + 6.dp.toPx()),
                        cornerRadius = CornerRadius(cornerPx + 4.dp.toPx())
                    )
                }

                // 3D Extruded Depth Base Layer
                drawRoundRect(
                    color = depthColor,
                    topLeft = Offset(0f, depthPx),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(cornerPx)
                )
            }
            .offset(y = (depthOffset - animatedDepth))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.94f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        borderColor
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * 3D Tactile Action Button with push animation, specular top highlight, and 3D depth shadow.
 */
@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = RichViolet,
    depthColor: Color = Color(0xFF4334A8),
    contentColor: Color = Color.White,
    shape: RoundedCornerShape = RoundedCornerShape(18.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedDepth by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 5.dp,
        animationSpec = tween(durationMillis = 100),
        label = "button3dDepth"
    )

    Box(
        modifier = modifier
            .drawBehind {
                val cornerPx = shape.topStart.toPx(size, this)
                val depthPx = animatedDepth.toPx()
                // 3D Shadow/Extrusion
                drawRoundRect(
                    color = depthColor,
                    topLeft = Offset(0f, depthPx),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(cornerPx)
                )
            }
            .offset(y = (5.dp - animatedDepth))
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        buttonColor.copy(alpha = 0.96f),
                        buttonColor
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Surface(color = Color.Transparent, contentColor = contentColor) {
            content()
        }
    }
}

/**
 * Premium 3D Progress Ring Gauge with floating score badge and animated arc.
 */
@Composable
fun ThreeDProgressGauge(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 12.dp,
    trackColor: Color = Color(0x33B8A8FF),
    gradientColors: List<Color> = listOf(Color(0xFF7965E8), Color(0xFFB7A8FF), Color(0xFF55E6C1)),
    centerText: String = "${(progress.coerceIn(0f, 1f) * 100).toInt()}%"
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge3dProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokePx
            val topLeft = Offset((this.size.width - diameter) / 2, (this.size.height - diameter) / 2)

            // Outer depth shadow ring
            drawArc(
                color = Color(0x1A18122B),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(topLeft.x, topLeft.y + 3.dp.toPx()),
                size = Size(diameter, diameter),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Track background ring
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Animated 3D Gradient Arc
            if (animatedProgress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = gradientColors,
                        center = Offset(this.size.width / 2, this.size.height / 2)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Inner 3D Sphere/Disc
        Surface(
            modifier = Modifier
                .size(size - strokeWidth * 2.2f)
                .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = RichViolet),
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerText,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "DONE",
                        color = RichViolet,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * 3D Capsule Pill / Badge for priority labels, tags, and stats.
 */
@Composable
fun ThreeDBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ElectricLavender.copy(alpha = 0.3f),
    textColor: Color = DeepMidnightPurple,
    icon: ImageVector? = null
) {
    Surface(
        modifier = modifier
            .shadow(4.dp, shape = CircleShape, spotColor = backgroundColor),
        shape = CircleShape,
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.8f), backgroundColor.copy(alpha = 0.5f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}
