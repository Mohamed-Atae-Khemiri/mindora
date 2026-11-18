package com.example.mindora.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_activities",
    indices = [
        Index(value = ["idempotencyKey"], unique = true),
        Index(value = ["localEpochDay"]),
        Index(value = ["occurredAtEpochMillis"])
    ]
)
data class StudyActivity(
    @PrimaryKey val activityId: String,
    val occurredAtEpochMillis: Long,
    val localEpochDay: Long,
    val timeZoneId: String,
    val source: String,
    val sourceId: String?,
    val idempotencyKey: String
)
