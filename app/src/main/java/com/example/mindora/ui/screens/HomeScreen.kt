package com.example.mindora.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.StudentAvatar
import com.example.mindora.ui.components.StudyStreakCard
import com.example.mindora.ui.components.TaskCard
import com.example.mindora.ui.components.ThreeDBadge
import com.example.mindora.ui.components.ThreeDButton
import com.example.mindora.ui.components.ThreeDGlassCard
import com.example.mindora.ui.components.ThreeDProgressGauge
import com.example.mindora.ui.components.WeeklyCalendarStrip
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.LimeAccent
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SoftCream
import com.example.mindora.ui.theme.SurfaceBorder
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary
import com.example.mindora.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTasks: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    val state by viewModel.dashboardState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val milestoneToCelebrate = state.pendingMilestones.firstOrNull()

    LaunchedEffect(milestoneToCelebrate?.id) {
        milestoneToCelebrate?.let { milestone ->
            snackbarHostState.showSnackbar(
                "Panda Study celebrates your " + milestone.milestoneDays + "-day streak!"
            )
            viewModel.markMilestoneCelebrated(milestone.id)
        }
    }

    val progress by animateFloatAsState(
        targetValue = state.overallProgress.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "dashboardProgress"
    )
    val date = SimpleDateFormat("EEEE · d MMMM", Locale.getDefault()).format(Date())

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    containerColor = DeepMidnightPurple,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PandaStudyMascot(size = 38.dp, mood = PandaMood.CELEBRATING, animate = false)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            snackbarData.visuals.message,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ThreeDButton(
                onClick = onAddTaskClick,
                buttonColor = DeepMidnightPurple,
                depthColor = Color(0xFF140F26),
                shape = RoundedCornerShape(22.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", tint = ElectricLavender)
                    Spacer(Modifier.width(8.dp))
                    Text("Make a task", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { insets ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentAlignment = Alignment.TopCenter
        ) {
            val pageGutter = if (maxWidth >= 720.dp) 40.dp else 22.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                contentPadding = PaddingValues(bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Greeting
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = pageGutter)
                            .padding(top = 18.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    color = DeepMidnightPurple,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricLavender.copy(alpha = 0.4f))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        PandaStudyMascot(size = 36.dp, mood = PandaMood.HAPPY, animate = false)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "MINDORA",
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        "STUDY STUDIO",
                                        color = RichViolet,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.6.sp
                                    )
                                }
                            }
                            StudentAvatar(state.profile, size = 44.dp)
                        }

                        Spacer(Modifier.height(28.dp))
                        ThreeDBadge(
                            text = date.uppercase(),
                            backgroundColor = ElectricLavender.copy(alpha = 0.25f),
                            textColor = DeepMidnightPurple
                        )
                        state.profile?.let { profile ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "WELCOME BACK, " + profile.fullName.substringBefore(" ").uppercase(),
                                color = RichViolet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "A good day",
                            color = TextPrimary,
                            fontSize = 42.sp,
                            lineHeight = 44.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "to get curious.",
                                color = RichViolet,
                                fontSize = 38.sp,
                                lineHeight = 42.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-2).sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Small steps make a remarkable semester.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "THE WEEK AHEAD",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(Modifier.width(10.dp))
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(TextPrimary.copy(alpha = 0.15f))
                            )
                            Spacer(Modifier.width(10.dp))
                            Icon(Icons.Default.CalendarMonth, null, tint = RichViolet, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        WeeklyCalendarStrip()
                    }
                }

                // 3D Daily Edition Card
                item {
                    ThreeDGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = pageGutter),
                        backgroundColor = DeepMidnightPurple,
                        depthColor = Color(0xFF140F26),
                        depthOffset = 8.dp,
                        glowColor = RichViolet
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Ambient Orbs
                            val electricLavenderColor = ElectricLavender
                            val coralColor = CoralAccent
                            val limeColor = LimeAccent
                            Canvas(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(180.dp)
                                    .offset(x = 10.dp, y = (-10).dp)
                            ) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(electricLavenderColor.copy(alpha = 0.35f), Color.Transparent)
                                    ),
                                    radius = size.minDimension * 0.55f,
                                    center = Offset(size.width * 0.7f, size.height * 0.3f)
                                )
                                drawCircle(
                                    color = coralColor,
                                    radius = 8.dp.toPx(),
                                    center = Offset(size.width * 0.75f, size.height * 0.2f)
                                )
                                drawCircle(
                                    color = limeColor,
                                    radius = 5.dp.toPx(),
                                    center = Offset(size.width * 0.5f, size.height * 0.6f)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "YOUR DAILY EDITION",
                                        color = ElectricLavender,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.6.sp
                                    )
                                    ThreeDBadge(
                                        text = "DAILY ACTIVE",
                                        backgroundColor = Color.White.copy(alpha = 0.15f),
                                        textColor = LimeAccent
                                    )
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            state.completedTasksCount.toString().padStart(2, '0'),
                                            color = Color.White,
                                            fontSize = 68.sp,
                                            lineHeight = 68.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = (-4).sp
                                        )
                                        Text(
                                            "things moved forward",
                                            color = Color.White.copy(alpha = 0.75f),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    ThreeDProgressGauge(
                                        progress = progress,
                                        size = 92.dp,
                                        strokeWidth = 11.dp
                                    )
                                }

                                Spacer(Modifier.height(18.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (state.pendingTasksCount == 0) "The slate is clear."
                                        else "${state.pendingTasksCount} tasks still in your orbit",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        Icons.Default.ArrowOutward,
                                        contentDescription = null,
                                        tint = LimeAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3D Bento Numbers
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = pageGutter)
                    ) {
                        Text(
                            "A FEW USEFUL NUMBERS",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.6.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ThreeDGlassCard(
                                modifier = Modifier.weight(1f),
                                depthOffset = 5.dp
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("01", color = RichViolet, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        Icon(Icons.Default.Book, null, tint = RichViolet, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        state.subjectsCount.toString(),
                                        color = TextPrimary,
                                        fontSize = 32.sp,
                                        lineHeight = 34.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        "subjects in play",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            ThreeDGlassCard(
                                modifier = Modifier.weight(1f),
                                depthOffset = 5.dp
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("02", color = CoralAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        Icon(Icons.Default.Schedule, null, tint = CoralAccent, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        state.pendingTasksCount.toString(),
                                        color = TextPrimary,
                                        fontSize = 32.sp,
                                        lineHeight = 34.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        "tasks in the queue",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        ThreeDGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = ElectricLavender.copy(alpha = 0.25f),
                            depthOffset = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "SEMESTER MOMENTUM",
                                        color = DeepMidnightPurple.copy(alpha = 0.7f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${state.completedTasksCount} complete · ${state.pendingTasksCount} to go",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    null,
                                    tint = RichViolet,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // 3D Study Streak Card
                item(key = "study-streak") {
                    StudyStreakCard(
                        streak = state.streak,
                        modifier = Modifier.padding(horizontal = pageGutter)
                    )
                }

                // Urgent Tasks Section
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = pageGutter, end = pageGutter, top = 12.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = CoralAccent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Worth tackling",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.weight(1f))
                        if (state.urgentTasks.isNotEmpty()) {
                            ThreeDBadge(
                                text = state.urgentTasks.size.toString().padStart(2, '0'),
                                backgroundColor = ErrorRed.copy(alpha = 0.15f),
                                textColor = ErrorRed
                            )
                        }
                    }
                }

                if (state.urgentTasks.isEmpty()) {
                    item {
                        ThreeDGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = pageGutter),
                            depthOffset = 3.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PandaStudyMascot(size = 46.dp, mood = PandaMood.SLEEPING, animate = false)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text("No fires to put out.", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("A rare and lovely kind of quiet.", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(state.urgentTasks) { entry ->
                        val deadline = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.task.deadline))
                        TaskCard(
                            modifier = Modifier.padding(horizontal = pageGutter),
                            title = entry.task.title,
                            description = entry.task.description,
                            subjectName = entry.subject?.name ?: "General",
                            subjectColorHex = entry.subject?.color ?: "#7965E8",
                            taskType = entry.task.type,
                            deadlineDate = deadline,
                            priorityLabel = viewModel.priorityFor(entry.task.isCompleted, entry.task.deadline, entry.task.priority),
                            isCompleted = entry.task.isCompleted,
                            onToggleCompleted = { completed ->
                                viewModel.toggleTaskCompletion(entry.task.id, completed)
                                if (completed) coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Panda Study is proud of your progress!")
                                }
                            },
                            onEditClick = onNavigateToTasks,
                            onDeleteClick = {}
                        )
                    }
                }

                // Upcoming Tasks Section
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = pageGutter, end = pageGutter, top = 12.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "On the horizon",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.weight(1f))
                        Text("${state.upcomingTasks.size} scheduled", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (state.upcomingTasks.isEmpty()) {
                    item {
                        ThreeDGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = pageGutter),
                            depthOffset = 3.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PandaStudyMascot(size = 46.dp, mood = PandaMood.SLEEPING, animate = false)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text("Room to breathe.", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("No deadlines are waiting this week.", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(state.upcomingTasks) { entry ->
                        val deadline = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.task.deadline))
                        TaskCard(
                            modifier = Modifier.padding(horizontal = pageGutter),
                            title = entry.task.title,
                            description = entry.task.description,
                            subjectName = entry.subject?.name ?: "General",
                            subjectColorHex = entry.subject?.color ?: "#7965E8",
                            taskType = entry.task.type,
                            deadlineDate = deadline,
                            priorityLabel = viewModel.priorityFor(entry.task.isCompleted, entry.task.deadline, entry.task.priority),
                            isCompleted = entry.task.isCompleted,
                            onToggleCompleted = { completed ->
                                viewModel.toggleTaskCompletion(entry.task.id, completed)
                                if (completed) coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Panda Study is proud of your progress!")
                                }
                            },
                            onEditClick = onNavigateToTasks,
                            onDeleteClick = {}
                        )
                    }
                }
            }
        }
    }
}
