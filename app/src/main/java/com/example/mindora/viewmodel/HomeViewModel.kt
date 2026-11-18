package com.example.mindora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.data.entity.StudyMilestone
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.data.entity.Subject
import com.example.mindora.data.entity.TaskWithSubject
import com.example.mindora.data.repository.StudentProfileRepository
import com.example.mindora.data.repository.StudyActivityRepository
import com.example.mindora.data.repository.SubjectRepository
import com.example.mindora.data.repository.TaskRepository
import com.example.mindora.domain.model.SmartPriorityLabel
import com.example.mindora.domain.usecase.CalculateSmartPriority
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn

data class HomeDashboardState(
    val subjectsCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val overallProgress: Float = 0f,
    val urgentTasks: List<TaskWithSubject> = emptyList(),
    val upcomingTasks: List<TaskWithSubject> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val profile: StudentProfile? = null,
    val streak: StudyStreakState = StudyStreakState(),
    val pendingMilestones: List<StudyMilestone> = emptyList()
)

class HomeViewModel(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    profileRepository: StudentProfileRepository,
    private val studyActivityRepository: StudyActivityRepository
) : ViewModel() {
    private val currentDay: Flow<Long> = flow {
        while (true) {
            emit(StudyActivityRepository.localEpochDay())
            delay(60_000)
        }
    }

    private val dashboardCore = combine(
        subjectRepository.allSubjects,
        taskRepository.allTasksWithSubject,
        profileRepository.profile,
        studyActivityRepository.studyDays,
        currentDay
    ) { subjects, tasks, profile, activityDays, today ->
        val totalTasks = tasks.size
        val completed = tasks.count { it.task.isCompleted }
        val urgent = tasks.filter {
            !it.task.isCompleted && CalculateSmartPriority.invoke(it.task.isCompleted, it.task.deadline, it.task.priority) == SmartPriorityLabel.URGENT
        }
        val upcoming = tasks.filter {
            !it.task.isCompleted && CalculateSmartPriority.invoke(it.task.isCompleted, it.task.deadline, it.task.priority) == SmartPriorityLabel.UPCOMING
        }
        HomeDashboardState(
            subjectsCount = subjects.size,
            pendingTasksCount = totalTasks - completed,
            completedTasksCount = completed,
            overallProgress = if (totalTasks > 0) completed.toFloat() / totalTasks else 0f,
            urgentTasks = urgent,
            upcomingTasks = upcoming,
            subjects = subjects,
            profile = profile,
            streak = buildStudyStreakState(activityDays, today)
        )
    }

    val dashboardState: StateFlow<HomeDashboardState> = combine(
        dashboardCore,
        studyActivityRepository.pendingMilestones
    ) { core, milestones -> core.copy(pendingMilestones = milestones) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeDashboardState()
    )

    fun priorityFor(isCompleted: Boolean, deadlineMillis: Long, priority: String): SmartPriorityLabel =
        CalculateSmartPriority.invoke(isCompleted, deadlineMillis, priority)

    suspend fun markMilestoneCelebrated(id: Long) {
        studyActivityRepository.markMilestoneCelebrated(id)
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskCompletion(taskId, isCompleted)
        }
    }
}
