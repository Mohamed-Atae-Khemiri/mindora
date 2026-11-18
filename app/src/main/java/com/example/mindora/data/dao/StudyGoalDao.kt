package com.example.mindora.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindora.data.entity.StudyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: StudyGoal): Long

    @Update
    suspend fun updateGoal(goal: StudyGoal)

    @Delete
    suspend fun deleteGoal(goal: StudyGoal)

    @Query("SELECT * FROM study_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<StudyGoal>>
}
