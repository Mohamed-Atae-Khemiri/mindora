package com.example.mindora.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.TaskCard
import com.example.mindora.ui.components.StudentAvatar
import com.example.mindora.ui.components.StudyStreakCard
import com.example.mindora.ui.components.WeeklyCalendarStrip
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.LimeAccent
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SoftCream
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextSecondary
import com.example.mindora.viewmodel.HomeViewModel
import com.example.mindora.viewmodel.PriorityUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val StudioInk: Color @Composable get() = TextPrimary
private val StudioPaper: Color @Composable get() = SoftCream
private val StudioOrange: Color @Composable get() = CoralAccent
private val StudioLilac: Color @Composable get() = ElectricLavender
private val StudioMint: Color @Composable get() = LimeAccent

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
        containerColor = StudioPaper,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PandaStudyMascot(size = 40.dp, mood = PandaMood.CELEBRATING, animate = false)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            snackbarData.visuals.message,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = DeepMidnightPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomEnd = 22.dp, bottomStart = 5.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
                Spacer(Modifier.width(8.dp))
                Text("Make a task", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    ) { insets ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(insets),
            contentAlignment = Alignment.TopCenter
        ) {
            val pageGutter = if (maxWidth >= 720.dp) 40.dp else 24.dp
            LazyColumn(
                modifier = Modifier.fillMaxWidth().widthIn(max = 840.dp),
            contentPadding = PaddingValues(bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = pageGutter).padding(top = 20.dp, bottom = 18.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PandaStudyMascot(size = 40.dp, mood = PandaMood.HAPPY, animate = false)
                            Spacer(Modifier.width(9.dp))
                            Column {
                                Text(
                                    "MINDORA",
                                    color = StudioInk,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.8.sp
                                )
                                Text(
                                    "STUDY STUDIO",
                                    color = TextMuted,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp
                                )
                            }
                        }
                        StudentAvatar(state.profile, size = 42.dp)
                    }

                    Spacer(Modifier.height(34.dp))
                    Text(date.uppercase(), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                    state.profile?.let { profile ->
                        Spacer(Modifier.height(8.dp))
                        Text("GOOD TO SEE YOU, " + profile.fullName.substringBefore(" ").uppercase(), color = RichViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("A good day", color = StudioInk, fontSize = 44.sp, lineHeight = 44.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("to get curious.", color = RichViolet, fontSize = 40.sp, lineHeight = 43.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp, modifier = Modifier.weight(1f))
                        Text("NO. 01", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.3.sp, modifier = Modifier.padding(bottom = 7.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Small steps make a remarkable semester.", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(26.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("THE WEEK AHEAD", color = StudioInk, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Spacer(Modifier.width(10.dp))
                        Box(Modifier.weight(1f).height(1.dp).background(StudioInk.copy(alpha = .18f)))
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Default.CalendarMonth, null, tint = RichViolet, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    WeeklyCalendarStrip()
                }
            }

            item {
                Box(Modifier.fillMaxWidth().padding(horizontal = pageGutter, vertical = 8.dp)) {
                    Box(Modifier.fillMaxWidth().height(226.dp).clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomEnd = 30.dp, bottomStart = 5.dp)).background(DeepMidnightPurple))
                    val orbLilac = StudioLilac
                    val orbOrange = StudioOrange
                    val orbMint = StudioMint
                    Canvas(Modifier.align(Alignment.TopEnd).size(190.dp).offset(x = (-4).dp, y = 2.dp)) {
                        val center = Offset(size.width * .70f, size.height * .32f)
                        drawCircle(orbLilac.copy(alpha = .16f), radius = size.minDimension * .48f, center = center)
                        drawCircle(orbOrange, radius = 9.dp.toPx(), center = Offset(size.width * .78f, size.height * .18f))
                        drawCircle(orbMint, radius = 4.dp.toPx(), center = Offset(size.width * .54f, size.height * .55f))
                    }
                    Column(Modifier.fillMaxWidth().height(226.dp).padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("YOUR DAILY EDITION", color = StudioLilac, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                            Text("01 / 03", color = Color.White.copy(alpha = .55f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Column(Modifier.weight(1f)) {
                                Text(state.completedTasksCount.toString().padStart(2, '0'), color = Color.White, fontSize = 70.sp, lineHeight = 72.sp, fontWeight = FontWeight.Black, letterSpacing = (-4).sp)
                                Text("things moved forward", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                            }
                            Box(Modifier.padding(bottom = 4.dp).size(72.dp).clip(CircleShape).background(StudioMint), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text((progress * 100).toInt().toString() + "%", color = DeepMidnightPurple, fontSize = 19.sp, fontWeight = FontWeight.Black)
                                    Text("DONE", color = DeepMidnightPurple.copy(alpha = .82f), fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (state.pendingTasksCount == 0) "The slate is clear." else state.pendingTasksCount.toString() + " still in your orbit", color = Color.White, fontSize = 11.sp)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = StudioMint, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = pageGutter).padding(top = 30.dp, bottom = 6.dp)) {
                    Text("A FEW USEFUL NUMBERS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.6.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        MetricColumn("01", state.subjectsCount.toString(), "subjects in play", RichViolet, Modifier.weight(1f)) {
                            Icon(Icons.Default.Book, null, tint = RichViolet, modifier = Modifier.size(18.dp))
                        }
                        Box(Modifier.width(1.dp).height(72.dp).background(StudioInk.copy(alpha = .16f)))
                        MetricColumn("02", state.pendingTasksCount.toString(), "tasks in the queue", StudioOrange, Modifier.weight(1f)) {
                            Icon(Icons.Default.Schedule, null, tint = StudioOrange, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)).background(StudioLilac.copy(alpha = .38f)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("SEMESTER MOMENTUM", color = StudioInk.copy(alpha = .62f), fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(state.completedTasksCount.toString() + " complete · " + state.pendingTasksCount.toString() + " to go", color = StudioInk, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = RichViolet, modifier = Modifier.size(23.dp))
                    }
                }
            }

            item(key = "study-streak") {
                StudyStreakCard(
                    streak = state.streak,
                    modifier = Modifier.padding(horizontal = pageGutter, vertical = 10.dp)
                )
            }

            item {
                Row(Modifier.fillMaxWidth().padding(start = pageGutter, end = pageGutter, top = 28.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = StudioOrange, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Worth tackling", color = StudioInk, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = (-.5).sp)
                    Spacer(Modifier.weight(1f))
                    if (state.urgentTasks.isNotEmpty()) Text(state.urgentTasks.size.toString().padStart(2, '0'), color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
            if (state.urgentTasks.isEmpty()) {
                item { EmptyNote(Modifier.padding(horizontal = pageGutter, vertical = 6.dp), "No fires to put out.", "A rare and lovely kind of quiet.", PandaMood.SLEEPING) }
            } else {
                items(state.urgentTasks) { entry ->
                    val deadline = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.task.deadline))
                    TaskCard(
                        modifier = Modifier.padding(horizontal = pageGutter, vertical = 6.dp),
                        title = entry.task.title,
                        description = entry.task.description,
                        subjectName = entry.subject?.name ?: "General",
                        subjectColorHex = entry.subject?.color ?: "#7965E8",
                        taskType = entry.task.type,
                        deadlineDate = deadline,
                        priorityLabel = PriorityUtils.calculateSmartPriority(entry.task),
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

            item {
                Row(Modifier.fillMaxWidth().padding(start = pageGutter, end = pageGutter, top = 25.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("On the horizon", color = StudioInk, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = (-.5).sp)
                    Spacer(Modifier.weight(1f))
                    Text(state.upcomingTasks.size.toString() + " scheduled", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (state.upcomingTasks.isEmpty()) {
                item { EmptyNote(Modifier.padding(horizontal = pageGutter, vertical = 6.dp), "Room to breathe.", "No deadlines are waiting this week.", PandaMood.SLEEPING) }
            } else {
                items(state.upcomingTasks) { entry ->
                    val deadline = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.task.deadline))
                    TaskCard(
                        modifier = Modifier.padding(horizontal = pageGutter, vertical = 6.dp),
                        title = entry.task.title,
                        description = entry.task.description,
                        subjectName = entry.subject?.name ?: "General",
                        subjectColorHex = entry.subject?.color ?: "#7965E8",
                        taskType = entry.task.type,
                        deadlineDate = deadline,
                        priorityLabel = PriorityUtils.calculateSmartPriority(entry.task),
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
            item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    index: String,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier,
    icon: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(index, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            icon()
        }
        Spacer(Modifier.height(8.dp))
        Text(value, color = StudioInk, fontSize = 32.sp, lineHeight = 34.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
        Text(label, color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun EmptyNote(modifier: Modifier, title: String, detail: String, mood: PandaMood) {
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)).background(AppSurface).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        PandaStudyMascot(size = 48.dp, mood = mood, animate = false)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = StudioInk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
