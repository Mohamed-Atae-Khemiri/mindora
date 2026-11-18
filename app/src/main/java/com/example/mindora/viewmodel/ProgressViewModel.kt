package com.example.mindora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.data.entity.StudyGoal
import com.example.mindora.data.repository.StudyGoalRepository
import com.example.mindora.data.repository.StudyActivityRepository
import com.example.mindora.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AcademicProgressState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionPercentage: Float = 0f,
    val studyGoals: List<StudyGoal> = emptyList()
)

class ProgressViewModel(
    private val taskRepository: TaskRepository,
    private val studyGoalRepository: StudyGoalRepository,
    private val studyActivityRepository: StudyActivityRepository
) : ViewModel() {

    val progressState: StateFlow<AcademicProgressState> = combine(
        taskRepository.allTasksWithSubject,
        studyGoalRepository.allGoals
    ) { tasks, goals ->
        val total = tasks.size
        val completed = tasks.count { it.task.isCompleted }
        val pending = total - completed
        val percentage = if (total > 0) completed.toFloat() / total else 0f

        AcademicProgressState(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            completionPercentage = percentage,
            studyGoals = goals
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AcademicProgressState()
    )

    fun addStudyGoal(title: String, target: Int) {
        viewModelScope.launch {
            val goal = StudyGoal(
                title = title,
                target = target,
                completed = 0
            )
            studyGoalRepository.insertGoal(goal)
        }
    }

    fun updateGoalProgress(goal: StudyGoal, newCompleted: Int) {
        viewModelScope.launch {
            val updated = goal.copy(completed = newCompleted.coerceIn(0, goal.target))
            studyGoalRepository.updateGoal(updated)
            if (updated.completed > goal.completed) {
                studyActivityRepository.recordCompletedStudyActivity(
                    source = "study_goal_progress",
                    sourceId = goal.id.toString()
                )
            }
        }
    }

    fun deleteStudyGoal(goal: StudyGoal) {
        viewModelScope.launch {
            studyGoalRepository.deleteGoal(goal)
        }
    }
}
