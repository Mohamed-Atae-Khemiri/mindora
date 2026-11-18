package com.example.mindora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.data.entity.StudyGoal
import com.example.mindora.ui.components.MindoraFeatureShape
import com.example.mindora.ui.components.MindoraPage
import com.example.mindora.ui.components.MindoraSectionHeading
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.MindoraSurfaceShape
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.LimeAccent
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SurfaceBorder
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary
import com.example.mindora.ui.theme.WarningOrange
import com.example.mindora.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val state by viewModel.progressState.collectAsState()
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<StudyGoal?>(null) }

    MindoraPage(
        eyebrow = "Your semester, in motion",
        title = "Progress",
        description = "Notice the work you have already put in.",
        headerAction = {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LimeAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = "Your progress", tint = DeepMidnightPurple)
            }
        }
    ) {
        item(key = "progress-feature") {
            val percentage = state.completionPercentage.coerceIn(0f, 1f)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MindoraFeatureShape)
                    .background(DeepMidnightPurple)
                    .padding(horizontal = 22.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "THE LONG GAME",
                            color = ElectricLavender,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.7.sp
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "${(percentage * 100).toInt()}%",
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontSize = 54.sp,
                            lineHeight = 58.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.5).sp
                        )
                        Text(
                            "of your tasks are complete",
                            color = Color.White.copy(alpha = .78f),
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        Modifier
                            .padding(top = 4.dp)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(CoralAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = DeepMidnightPurple)
                    }
                }
                Spacer(Modifier.height(20.dp))
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = LimeAccent,
                    trackColor = Color.White.copy(alpha = .2f)
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressMetric("TOTAL", state.totalTasks.toString(), Modifier.weight(1f))
                    ProgressMetric("DONE", state.completedTasks.toString(), Modifier.weight(1f))
                    ProgressMetric("TO GO", state.pendingTasks.toString(), Modifier.weight(1f))
                }
            }
        }

        item(key = "weekly-goals-heading") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MindoraSectionHeading(
                    title = "Weekly goals",
                    detail = "Small promises, kept consistently.",
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = { showAddGoalDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("New")
                }
            }
        }

        if (state.studyGoals.isEmpty()) {
            item(key = "goals-empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MindoraSurfaceShape)
                        .background(AppSurface)
                        .padding(horizontal = 24.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    PandaStudyMascot(
                        size = 76.dp,
                        mood = PandaMood.STUDYING,
                        animate = false
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "MAKE IT YOURS",
                        color = RichViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Choose one thing to practice this week.",
                        color = TextPrimary,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Set a study target, then check in as you go.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            items(
                count = state.studyGoals.size,
                key = { index -> state.studyGoals[index].id }
            ) { index ->
                val goal = state.studyGoals[index]
                GoalCard(
                    goal = goal,
                    onIncrement = { viewModel.updateGoalProgress(goal, goal.completed + 1) },
                    onDecrement = { viewModel.updateGoalProgress(goal, goal.completed - 1) },
                    onDelete = { goalToDelete = goal }
                )
            }
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { title, target ->
                viewModel.addStudyGoal(title, target)
                showAddGoalDialog = false
            }
        )
    }

    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete goal", fontWeight = FontWeight.Bold) },
            text = { Text("Delete “${goal.title}”? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudyGoal(goal)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { goalToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ProgressMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, color = ElectricLavender, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GoalCard(
    goal: StudyGoal,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (goal.target > 0) goal.completed.toFloat() / goal.target else 0f
    val completed = goal.target > 0 && goal.completed >= goal.target

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MindoraSurfaceShape,
        color = AppSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (completed) LimeAccent else ElectricLavender.copy(alpha = .4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (completed) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = if (completed) "Goal complete" else "Goal in progress",
                        tint = DeepMidnightPurple,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(goal.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "${goal.completed} of ${goal.target} completed",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete goal", tint = ErrorRed)
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape),
                color = if (completed) RichViolet else WarningOrange,
                trackColor = ElectricLavender.copy(alpha = .25f)
            )
            Spacer(Modifier.height(7.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrement, enabled = goal.completed > 0) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease progress", tint = RichViolet)
                }
                Text(
                    "${goal.completed} / ${goal.target}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onIncrement, enabled = goal.completed < goal.target) {
                    Icon(Icons.Default.Add, contentDescription = "Increase progress", tint = RichViolet)
                }
            }
        }
    }
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New weekly goal", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal title") },
                    placeholder = { Text("e.g. Review lecture notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it.filter(Char::isDigit) },
                    label = { Text("Target count") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toIntOrNull() ?: 1
                    if (title.isNotBlank() && target > 0) onConfirm(title.trim(), target)
                },
                enabled = title.isNotBlank() && (targetText.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = DeepMidnightPurple)
            ) {
                Text("Save goal", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
