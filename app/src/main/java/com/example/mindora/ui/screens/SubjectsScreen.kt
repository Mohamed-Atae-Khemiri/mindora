package com.example.mindora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.example.mindora.data.entity.Subject
import com.example.mindora.ui.components.MindoraPage
import com.example.mindora.ui.components.MindoraSectionHeading
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.SubjectCard
import com.example.mindora.ui.components.ThreeDBadge
import com.example.mindora.ui.components.ThreeDButton
import com.example.mindora.ui.components.ThreeDGlassCard
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.SubjectColorPresets
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary
import com.example.mindora.viewmodel.SubjectViewModel

@Composable
fun SubjectsScreen(viewModel: SubjectViewModel) {
    val subjectsWithCount by viewModel.subjectsWithCount.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    MindoraPage(
        eyebrow = "Your course library",
        title = "Subjects",
        description = "A clear place for every class, idea and deadline.",
        floatingActionButton = {
            ThreeDButton(
                onClick = { showAddDialog = true },
                buttonColor = DeepMidnightPurple,
                depthColor = Color(0xFF140F26),
                shape = RoundedCornerShape(22.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add subject", tint = ElectricLavender)
                    Spacer(Modifier.width(8.dp))
                    Text("New subject", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }
            }
        }
    ) {
        if (subjectsWithCount.isEmpty()) {
            item(key = "subjects-empty") {
                ThreeDGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    depthOffset = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 34.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PandaStudyMascot(size = 82.dp, mood = PandaMood.STUDYING, animate = false)
                        Spacer(Modifier.height(18.dp))
                        Text(
                            "Start with one class",
                            color = TextPrimary,
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "Add a subject to group its assignments, homework and exams.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            item(key = "subjects-overview") {
                ThreeDGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DeepMidnightPurple,
                    depthColor = Color(0xFF140F26),
                    depthOffset = 8.dp,
                    glowColor = RichViolet
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "IN YOUR ORBIT",
                                color = ElectricLavender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.6.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                subjectsWithCount.size.toString().padStart(2, '0'),
                                color = Color.White,
                                fontSize = 42.sp,
                                lineHeight = 44.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-2).sp
                            )
                            Text(
                                if (subjectsWithCount.size == 1) "subject to explore" else "subjects to explore",
                                color = Color.White.copy(alpha = .78f),
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CoralAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = DeepMidnightPurple, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
            item(key = "subjects-section") {
                MindoraSectionHeading(
                    title = "The semester, sorted",
                    detail = "Open any subject to edit its details."
                )
            }
            items(
                count = subjectsWithCount.size,
                key = { index -> subjectsWithCount[index].subject.id }
            ) { index ->
                val item = subjectsWithCount[index]
                SubjectCard(
                    name = item.subject.name,
                    description = item.subject.description,
                    colorHex = item.subject.color,
                    taskCount = item.taskCount,
                    onClick = { subjectToEdit = item.subject },
                    onEditClick = { subjectToEdit = item.subject },
                    onDeleteClick = { subjectToDelete = item.subject },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showAddDialog) {
        SubjectFormDialog(
            title = "Add a subject",
            initialName = "",
            initialDescription = "",
            initialColor = SubjectColorPresets.first(),
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, color ->
                viewModel.addSubject(name, desc, color)
                showAddDialog = false
            }
        )
    }

    subjectToEdit?.let { subject ->
        SubjectFormDialog(
            title = "Edit subject",
            initialName = subject.name,
            initialDescription = subject.description,
            initialColor = subject.color,
            onDismiss = { subjectToEdit = null },
            onConfirm = { name, desc, color ->
                viewModel.updateSubject(subject.copy(name = name, description = desc, color = color))
                subjectToEdit = null
            }
        )
    }

    subjectToDelete?.let { subject ->
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Delete subject", fontWeight = FontWeight.Bold) },
            text = {
                Text("Delete ${subject.name}? Tasks associated with this subject will also be deleted.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(subject)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RichViolet)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectFormDialog(
    title: String,
    initialName: String,
    initialDescription: String,
    initialColor: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Choose an accent", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SubjectColorPresets.forEach { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (_: Exception) {
                            RichViolet
                        }
                        val isSelected = selectedColor.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) TextPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    Modifier
                                        .size(11.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim(), description.trim(), selectedColor)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepMidnightPurple)
            ) {
                Text("Save subject", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
