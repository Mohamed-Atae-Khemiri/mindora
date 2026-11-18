package com.example.mindora.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE // Foreign Key constraint: Deleting a subject deletes all its associated tasks automatically
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val deadline: Long, // Epoch timestamp in milliseconds
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val type: String = "ASSIGNMENT", // "ASSIGNMENT", "HOMEWORK", "EXAM"
    val isCompleted: Boolean = false,
    val subjectId: Long
)
