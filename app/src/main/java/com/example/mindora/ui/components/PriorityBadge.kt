package com.example.mindora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.ErrorRedLight
import com.example.mindora.ui.theme.PrimaryPurple
import com.example.mindora.ui.theme.PrimaryPurpleLight
import com.example.mindora.ui.theme.SuccessGreen
import com.example.mindora.ui.theme.SuccessGreenLight
import com.example.mindora.ui.theme.WarningOrange
import com.example.mindora.ui.theme.WarningOrangeLight

enum class SmartPriorityLabel(val displayName: String) {
    URGENT("Urgent"),
    UPCOMING("Upcoming"),
    LOW_PRIORITY("Low priority"),
    COMPLETED("Completed")
}

@Composable
private fun SmartPriorityLabel.containerColor(): Color = when (this) {
    SmartPriorityLabel.URGENT -> ErrorRedLight
    SmartPriorityLabel.UPCOMING -> WarningOrangeLight
    SmartPriorityLabel.LOW_PRIORITY -> PrimaryPurpleLight
    SmartPriorityLabel.COMPLETED -> SuccessGreenLight
}

@Composable
private fun SmartPriorityLabel.contentColor(): Color = when (this) {
    SmartPriorityLabel.URGENT -> ErrorRed
    SmartPriorityLabel.UPCOMING -> WarningOrange
    SmartPriorityLabel.LOW_PRIORITY -> PrimaryPurple
    SmartPriorityLabel.COMPLETED -> SuccessGreen
}

@Composable
fun PriorityBadge(
    priorityLabel: SmartPriorityLabel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(priorityLabel.containerColor())
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = priorityLabel.displayName,
            color = priorityLabel.contentColor(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
