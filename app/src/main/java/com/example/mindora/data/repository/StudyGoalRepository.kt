package com.example.mindora.data.repository

import com.example.mindora.data.dao.StudyGoalDao
import com.example.mindora.data.entity.StudyGoal
import kotlinx.coroutines.flow.Flow

class StudyGoalRepository(private val studyGoalDao: StudyGoalDao) {

    val allGoals: Flow<List<StudyGoal>> = studyGoalDao.getAllGoals()

    suspend fun insertGoal(goal: StudyGoal): Long = studyGoalDao.insertGoal(goal)

    suspend fun updateGoal(goal: StudyGoal) = studyGoalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: StudyGoal) = studyGoalDao.deleteGoal(goal)
}
