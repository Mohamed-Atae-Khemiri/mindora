package com.example.mindora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.SoftCream
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary

/**
 * Shared page scaffold: a readable, centered content measure on tablets and comfortable
 * phone gutters. Each screen remains a lazy list so its content can grow without eager layout.
 */
@Composable
fun MindoraPage(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
    headerAction: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = SoftCream,
        floatingActionButton = { floatingActionButton?.invoke() }
    ) { insets ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .background(SoftCream),
            contentAlignment = Alignment.TopCenter
        ) {
            val horizontalGutter = if (maxWidth >= 840.dp) 40.dp else 24.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 880.dp),
                contentPadding = PaddingValues(
                    start = horizontalGutter,
                    top = 22.dp,
                    end = horizontalGutter,
                    bottom = 112.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item(key = "page-header") {
                    MindoraPageHeader(
                        eyebrow = eyebrow,
                        title = title,
                        description = description,
                        action = headerAction
                    )
                }
                content()
            }
        }
    }
}

@Composable
fun MindoraPageHeader(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(7.dp)
                    .width(7.dp)
                    .clip(CircleShape)
                    .background(CoralAccent)
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = eyebrow.uppercase(),
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            action?.invoke()
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontSize = 38.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1.1).sp,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun MindoraSectionHeading(
    title: String,
    detail: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.35).sp,
            modifier = Modifier.semantics { heading() }
        )
        if (detail != null) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = detail,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

val MindoraFeatureShape: RoundedCornerShape
    get() = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomEnd = 28.dp, bottomStart = 5.dp)

val MindoraSurfaceShape: RoundedCornerShape
    get() = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 5.dp)
