package com.example.mindora.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_goals")
data class StudyGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val target: Int, // e.g., target study hours or tasks
    val completed: Int = 0 // progress completed so far
)
