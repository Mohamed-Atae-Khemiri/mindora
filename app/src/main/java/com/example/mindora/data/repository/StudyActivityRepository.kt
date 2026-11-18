package com.example.mindora.data.repository

import com.example.mindora.data.dao.StudyActivityDao
import com.example.mindora.data.entity.StudyActivity
import com.example.mindora.data.entity.StudyMilestone
import com.example.mindora.data.streak.StudyStreakCalculator
import kotlinx.coroutines.flow.Flow
import java.util.TimeZone
import java.util.UUID

class StudyActivityRepository(private val dao: StudyActivityDao) {
    val studyDays: Flow<List<Long>> = dao.observeStudyDays()
    val activities: Flow<List<StudyActivity>> = dao.observeActivities()
    val pendingMilestones: Flow<List<StudyMilestone>> = dao.observePendingMilestones()

    suspend fun recordCompletedStudyActivity(
        source: String,
        sourceId: String?,
        occurredAtMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault(),
        idempotencyKey: String = UUID.randomUUID().toString()
    ) {
        val activity = StudyActivity(
            activityId = idempotencyKey,
            occurredAtEpochMillis = occurredAtMillis,
            localEpochDay = StudyStreakCalculator.localEpochDay(occurredAtMillis, timeZone),
            timeZoneId = timeZone.id,
            source = source,
            sourceId = sourceId,
            idempotencyKey = idempotencyKey
        )
        dao.insertActivity(activity)

        val today = localEpochDay()
        val days = dao.getStudyDaysSnapshot().toHashSet()
        val streak = StudyStreakCalculator.currentStreak(days, today)
        if (streak in MILESTONES) {
            dao.insertMilestone(
                StudyMilestone(
                    streakStartEpochDay = today - streak + 1,
                    milestoneDays = streak
                )
            )
        }
    }

    suspend fun markMilestoneCelebrated(id: Long) = dao.markCelebrated(id)

    companion object {
        val MILESTONES = setOf(7, 14, 30, 100)

        fun localEpochDay(): Long {
            val now = System.currentTimeMillis()
            return StudyStreakCalculator.localEpochDay(now, TimeZone.getDefault())
        }
    }
}
