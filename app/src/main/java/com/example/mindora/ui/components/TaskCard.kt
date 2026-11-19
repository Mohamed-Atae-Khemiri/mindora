package com.example.mindora.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.domain.model.SmartPriorityLabel
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.PrimaryPurple
import com.example.mindora.ui.theme.PrimaryPurpleLight
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    title: String,
    description: String,
    subjectName: String,
    subjectColorHex: String,
    taskType: String,
    deadlineDate: String,
    priorityLabel: SmartPriorityLabel,
    isCompleted: Boolean,
    onToggleCompleted: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectColor = try {
        Color(android.graphics.Color.parseColor(subjectColorHex))
    } catch (_: Exception) {
        PrimaryPurple
    }
    val cardBgColor by animateColorAsState(
        targetValue = if (isCompleted) AppSurface.copy(alpha = .78f) else AppSurface,
        label = "TaskCardBg"
    )

    ThreeDGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = cardBgColor,
        depthOffset = if (isCompleted) 2.dp else 5.dp,
        borderColor = if (isCompleted) Color(0x33B8A8FF) else subjectColor.copy(alpha = 0.35f)
    ) {
        Column(Modifier.padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onToggleCompleted,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryPurple,
                        uncheckedColor = TextMuted
                    )
                )
                Spacer(Modifier.width(4.dp))
                if (isCompleted) {
                    PandaStudyMascot(size = 30.dp, mood = PandaMood.CELEBRATING, animate = false)
                    Spacer(Modifier.width(6.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) TextMuted else TextPrimary,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (description.isNotBlank()) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            description,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit $title", tint = TextMuted)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete $title", tint = ErrorRed)
                }
            }

            Spacer(Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (subjectName.isNotBlank()) {
                    ThreeDBadge(
                        text = subjectName,
                        backgroundColor = subjectColor.copy(alpha = 0.15f),
                        textColor = subjectColor
                    )
                }
                ThreeDBadge(
                    text = taskType.lowercase().replaceFirstChar { it.uppercase() },
                    backgroundColor = PrimaryPurpleLight,
                    textColor = PrimaryPurple
                )
                PriorityBadge(priorityLabel = priorityLabel)
            }

            if (deadlineDate.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Deadline",
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Due $deadlineDate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }
}
