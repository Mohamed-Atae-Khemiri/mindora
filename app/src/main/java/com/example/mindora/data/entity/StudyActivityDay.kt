package com.example.mindora.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "study_activity_days")
data class StudyActivityDay(
    @PrimaryKey val epochDay: Long
)

@Entity(
    tableName = "study_milestones",
    indices = [Index(value = ["streakStartEpochDay", "milestoneDays"], unique = true)]
)
data class StudyMilestone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val streakStartEpochDay: Long,
    val milestoneDays: Int,
    val celebrated: Boolean = false
)
