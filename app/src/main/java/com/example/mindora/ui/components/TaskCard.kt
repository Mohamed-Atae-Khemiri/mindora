package com.example.mindora.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.PrimaryPurple
import com.example.mindora.ui.theme.PrimaryPurpleLight
import com.example.mindora.ui.theme.SurfaceBorder
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
        targetValue = if (isCompleted) AppSurface.copy(alpha = .68f) else AppSurface,
        label = "TaskCardBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MindoraFeatureShape,
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(start = 16.dp, end = 12.dp, top = 13.dp, bottom = 14.dp)) {
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
                        fontWeight = FontWeight.SemiBold,
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

            Spacer(Modifier.height(9.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (subjectName.isNotBlank()) {
                    MetadataPill(label = subjectName, foreground = subjectColor, background = subjectColor.copy(alpha = .12f), showDot = true)
                }
                MetadataPill(label = taskType.lowercase().replaceFirstChar { it.uppercase() }, foreground = PrimaryPurple, background = PrimaryPurpleLight)
                PriorityBadge(priorityLabel = priorityLabel)
            }

            if (deadlineDate.isNotBlank()) {
                Spacer(Modifier.height(11.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Deadline",
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Due $deadlineDate", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun MetadataPill(
    label: String,
    foreground: Color,
    background: Color,
    showDot: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        if (showDot) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(foreground))
            Spacer(Modifier.width(6.dp))
        }
        Text(label, color = foreground, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
