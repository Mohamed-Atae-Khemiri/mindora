package com.example.mindora.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.data.entity.Subject
import com.example.mindora.data.entity.Task
import com.example.mindora.data.entity.TaskWithSubject
import com.example.mindora.ui.components.MindoraFeatureShape
import com.example.mindora.ui.components.MindoraPage
import com.example.mindora.ui.components.MindoraSectionHeading
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.MindoraSurfaceShape
import com.example.mindora.ui.components.TaskCard
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.ErrorRed
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SurfaceBorder
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary

import com.example.mindora.viewmodel.TaskFilter
import com.example.mindora.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TasksScreen(viewModel: TaskViewModel) {
    val tasksWithSubject by viewModel.tasksWithSubject.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskWithSubject?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    MindoraPage(
        eyebrow = "The working list",
        title = "Tasks & exams",
        description = "A calm plan for the work that moves you forward.",
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DeepMidnightPurple,
                contentColor = Color.White,
                shape = MindoraFeatureShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
                Spacer(Modifier.width(8.dp))
                Text("New task", fontWeight = FontWeight.SemiBold)
            }
        }
    ) {
        item(key = "task-filters") {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TaskFilter.entries.forEach { filter ->
                    val selected = selectedFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                filter.displayName,
                                color = if (selected) Color.White else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = if (selected) {
                            { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepMidnightPurple,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = ElectricLavender,
                            containerColor = AppSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = SurfaceBorder,
                            selectedBorderColor = DeepMidnightPurple
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        item(key = "task-count") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MindoraSurfaceShape)
                    .background(DeepMidnightPurple)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        selectedFilter.displayName.uppercase(),
                        color = ElectricLavender,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        tasksWithSubject.size.toString().padStart(2, '0'),
                        color = Color.White,
                        fontSize = 38.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp
                    )
                    Text(
                        if (tasksWithSubject.size == 1) "task in this view" else "tasks in this view",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 13.sp
                    )
                }
                Box(
                    Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp))
                        .background(CoralAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = DeepMidnightPurple)
                }
            }
        }

        item(key = "task-section-title") {
            MindoraSectionHeading(
                title = if (tasksWithSubject.isEmpty()) "A little breathing room" else "On your list",
                detail = if (tasksWithSubject.isEmpty()) {
                    "Try another filter or add something you want to remember."
                } else {
                    "Tap the check to mark something complete."
                }
            )
        }

        if (tasksWithSubject.isEmpty()) {
            item(key = "tasks-empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MindoraFeatureShape)
                        .background(AppSurface)
                        .padding(horizontal = 26.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PandaStudyMascot(size = 78.dp, mood = PandaMood.SLEEPING, animate = false)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Nothing here just yet",
                        color = TextPrimary,
                        fontFamily = FontFamily.Serif,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Your next step can start with one small task.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(
                count = tasksWithSubject.size,
                key = { index -> tasksWithSubject[index].task.id }
            ) { index ->
                val item = tasksWithSubject[index]
                val deadline = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    .format(Date(item.task.deadline))
                TaskCard(
                    title = item.task.title,
                    description = item.task.description,
                    subjectName = item.subject?.name ?: "General",
                    subjectColorHex = item.subject?.color ?: "#7965E8",
                    taskType = item.task.type,
                    deadlineDate = deadline,
                    priorityLabel = viewModel.priorityFor(item.task.isCompleted, item.task.deadline, item.task.priority),
                    isCompleted = item.task.isCompleted,
                    onToggleCompleted = { viewModel.toggleTaskCompletion(item.task.id, it) },
                    onEditClick = { taskToEdit = item },
                    onDeleteClick = { taskToDelete = item.task }
                )
            }
        }
    }

    if (showAddDialog) {
        TaskFormDialog(
            title = "Add a task",
            subjects = subjects,
            initialTitle = "",
            initialDescription = "",
            initialType = "ASSIGNMENT",
            initialPriority = "MEDIUM",
            initialSubjectId = subjects.firstOrNull()?.id ?: 0L,
            initialDaysUntilDue = 3,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, type, priority, subjectId, deadline ->
                viewModel.addTask(title, desc, deadline, priority, type, subjectId)
                showAddDialog = false
            }
        )
    }

    taskToEdit?.let { item ->
        val daysLeft = maxOf(1L, TimeUnit.MILLISECONDS.toDays(item.task.deadline - System.currentTimeMillis())).toInt()
        TaskFormDialog(
            title = "Edit task",
            subjects = subjects,
            initialTitle = item.task.title,
            initialDescription = item.task.description,
            initialType = item.task.type,
            initialPriority = item.task.priority,
            initialSubjectId = item.task.subjectId,
            initialDaysUntilDue = daysLeft,
            onDismiss = { taskToEdit = null },
            onConfirm = { title, desc, type, priority, subjectId, deadline ->
                viewModel.updateTask(
                    item.task.copy(
                        title = title,
                        description = desc,
                        type = type,
                        priority = priority,
                        subjectId = subjectId,
                        deadline = deadline
                    )
                )
                taskToEdit = null
            }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete task", fontWeight = FontWeight.Bold) },
            text = { Text("Delete ${task.title}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RichViolet)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TaskFormDialog(
    title: String,
    subjects: List<Subject>,
    initialTitle: String,
    initialDescription: String,
    initialType: String,
    initialPriority: String,
    initialSubjectId: Long,
    initialDaysUntilDue: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Long, Long) -> Unit
) {
    var titleText by remember { mutableStateOf(initialTitle) }
    var descriptionText by remember { mutableStateOf(initialDescription) }
    var selectedType by remember { mutableStateOf(initialType) }
    var selectedPriority by remember { mutableStateOf(initialPriority) }
    var selectedSubjectId by remember { mutableLongStateOf(initialSubjectId) }
    var daysUntilDue by remember { mutableStateOf(initialDaysUntilDue.toString()) }
    var expandedSubjectMenu by remember { mutableStateOf(false) }
    val selectedSubject = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Task title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Description (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                if (subjects.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expandedSubjectMenu,
                        onExpandedChange = { expandedSubjectMenu = !expandedSubjectMenu }
                    ) {
                        OutlinedTextField(
                            value = selectedSubject?.name ?: "Select subject",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subject") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjectMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSubjectMenu,
                            onDismissRequest = { expandedSubjectMenu = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        selectedSubjectId = subject.id
                                        expandedSubjectMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text("Task type", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("ASSIGNMENT", "HOMEWORK", "EXAM").forEach { type ->
                        val selected = selectedType.equals(type, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { selectedType = type },
                            label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepMidnightPurple,
                                selectedLabelColor = Color.White,
                                containerColor = AppSurface
                            )
                        )
                    }
                }

                Text("Priority", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { priority ->
                        val selected = selectedPriority.equals(priority, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepMidnightPurple,
                                selectedLabelColor = Color.White,
                                containerColor = AppSurface
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = daysUntilDue,
                    onValueChange = { daysUntilDue = it.filter(Char::isDigit) },
                    label = { Text("Due in (days from now)") },
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = daysUntilDue.toIntOrNull() ?: 1
                    val deadline = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())
                    if (titleText.isNotBlank() && selectedSubjectId != 0L) {
                        onConfirm(
                            titleText.trim(),
                            descriptionText.trim(),
                            selectedType,
                            selectedPriority,
                            selectedSubjectId,
                            deadline
                        )
                    }
                },
                enabled = titleText.isNotBlank() && selectedSubjectId != 0L,
                colors = ButtonDefaults.buttonColors(containerColor = DeepMidnightPurple)
            ) {
                Text("Save task", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
