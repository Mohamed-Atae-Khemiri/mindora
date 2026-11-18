package com.example.mindora.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.R
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.ElectricLavender

enum class PandaMood {
    HAPPY,
    STUDYING,
    CELEBRATING,
    ENCOURAGING,
    SLEEPING
}

@Composable
fun PandaStudyMascot(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    mood: PandaMood = PandaMood.HAPPY,
    animate: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "pandaStudyMascot")
    val bounce by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (mood == PandaMood.CELEBRATING) -size.value * .035f else 0f,
        animationSpec = infiniteRepeatable(tween(580), RepeatMode.Reverse),
        label = "pandaCelebrationBounce"
    )
    val selectedMood = mood
    val celebrationLavender = ElectricLavender
    val celebrationOrange = CoralAccent
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                if (animate && selectedMood == PandaMood.CELEBRATING) translationY = bounce.dp.toPx()
            }
            .semantics {
                contentDescription = when (selectedMood) {
                    PandaMood.HAPPY -> "Panda Study, smiling student panda"
                    PandaMood.STUDYING -> "Panda Study, studying with a book"
                    PandaMood.CELEBRATING -> "Panda Study, celebrating a study streak"
                    PandaMood.ENCOURAGING -> "Panda Study, encouraging you to keep going"
                    PandaMood.SLEEPING -> "Panda Study, taking a little rest"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.panda_study),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(Modifier.fillMaxSize()) {
            val canvasSize = this.size
            when (selectedMood) {
                PandaMood.CELEBRATING -> {
                    sparkle(canvasSize.width * .20f, canvasSize.height * .28f, canvasSize.minDimension * .085f, celebrationLavender)
                    sparkle(canvasSize.width * .82f, canvasSize.height * .35f, canvasSize.minDimension * .065f, celebrationOrange)
                    sparkle(canvasSize.width * .80f, canvasSize.height * .72f, canvasSize.minDimension * .045f, celebrationLavender)
                }
                PandaMood.ENCOURAGING -> {
                    drawCircle(
                        color = celebrationOrange,
                        radius = canvasSize.minDimension * .045f,
                        center = androidx.compose.ui.geometry.Offset(canvasSize.width * .83f, canvasSize.height * .27f)
                    )
                    drawCircle(
                        color = celebrationLavender,
                        radius = canvasSize.minDimension * .035f,
                        center = androidx.compose.ui.geometry.Offset(canvasSize.width * .76f, canvasSize.height * .20f)
                    )
                }
                PandaMood.SLEEPING -> {
                    val eyeColor = Color(0xFFF7F3FF)
                    val stroke = canvasSize.minDimension * .025f
                    listOf(.36f, .66f).forEach { eyeX ->
                        val centerX = canvasSize.width * eyeX
                        val centerY = canvasSize.height * .47f
                        val radius = canvasSize.minDimension * .055f
                        val lid = Path().apply {
                            moveTo(centerX - radius, centerY)
                            cubicTo(centerX - radius * .35f, centerY + radius, centerX + radius * .35f, centerY + radius, centerX + radius, centerY)
                        }
                        drawPath(lid, eyeColor, style = Stroke(width = stroke, cap = StrokeCap.Round))
                    }
                }
                PandaMood.HAPPY, PandaMood.STUDYING -> Unit
            }
        }
        if (selectedMood == PandaMood.SLEEPING) {
            Text(
                text = "Zz",
                color = ElectricLavender,
                fontSize = (size.value * .17f).sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = (-size * .13f), y = size * .10f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.sparkle(
    x: Float,
    y: Float,
    radius: Float,
    color: Color
) {
    val center = androidx.compose.ui.geometry.Offset(x, y)
    val star = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x + radius * .18f, center.y - radius * .18f, center.x + radius, center.y)
        quadraticTo(center.x + radius * .18f, center.y + radius * .18f, center.x, center.y + radius)
        quadraticTo(center.x - radius * .18f, center.y + radius * .18f, center.x - radius, center.y)
        quadraticTo(center.x - radius * .18f, center.y - radius * .18f, center.x, center.y - radius)
        close()
    }
    drawPath(star, color)
}
