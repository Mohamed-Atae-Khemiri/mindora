package com.example.mindora.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindora.data.entity.StudyActivity
import com.example.mindora.data.entity.StudyMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivity(activity: StudyActivity): Long

    @Query("SELECT DISTINCT localEpochDay FROM study_activities ORDER BY localEpochDay")
    fun observeStudyDays(): Flow<List<Long>>

    @Query("SELECT DISTINCT localEpochDay FROM study_activities ORDER BY localEpochDay")
    suspend fun getStudyDaysSnapshot(): List<Long>

    @Query("SELECT * FROM study_activities ORDER BY occurredAtEpochMillis")
    fun observeActivities(): Flow<List<StudyActivity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMilestone(milestone: StudyMilestone): Long

    @Query("SELECT * FROM study_milestones WHERE celebrated = 0 ORDER BY id")
    fun observePendingMilestones(): Flow<List<StudyMilestone>>

    @Query("UPDATE study_milestones SET celebrated = 1 WHERE id = :id")
    suspend fun markCelebrated(id: Long)
}
