package com.example.mindora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.data.entity.Subject
import com.example.mindora.data.entity.Task
import com.example.mindora.data.entity.TaskWithSubject
import com.example.mindora.data.repository.SubjectRepository
import com.example.mindora.data.repository.TaskRepository
import com.example.mindora.domain.model.SmartPriorityLabel
import com.example.mindora.domain.usecase.CalculateSmartPriority
import com.example.mindora.notification.ReminderHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val displayName: String) {
    ALL("All Tasks"),
    PENDING("Pending"),
    COMPLETED("Completed"),
    EXAMS("Exams"),
    ASSIGNMENTS("Assignments"),
    HOMEWORK("Homework")
}

class TaskViewModel(
    application: Application,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository
) : AndroidViewModel(application) {

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    val subjects: StateFlow<List<Subject>> = subjectRepository.allSubjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasksWithSubject: StateFlow<List<TaskWithSubject>> = combine(
        taskRepository.allTasksWithSubject,
        _selectedFilter
    ) { tasks, filter ->
        when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.task.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.task.isCompleted }
            TaskFilter.EXAMS -> tasks.filter { it.task.type.equals("EXAM", ignoreCase = true) }
            TaskFilter.ASSIGNMENTS -> tasks.filter { it.task.type.equals("ASSIGNMENT", ignoreCase = true) }
            TaskFilter.HOMEWORK -> tasks.filter { it.task.type.equals("HOMEWORK", ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun priorityFor(isCompleted: Boolean, deadlineMillis: Long, priority: String): SmartPriorityLabel =
        CalculateSmartPriority.invoke(isCompleted, deadlineMillis, priority)

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun addTask(
        title: String,
        description: String,
        deadline: Long,
        priority: String,
        type: String,
        subjectId: Long
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                deadline = deadline,
                priority = priority,
                type = type,
                subjectId = subjectId
            )
            val insertedId = taskRepository.insertTask(task)

            val context = getApplication<Application>()
            val oneDayBefore = deadline - 86400_000L
            val oneHourBefore = deadline - 3600_000L

            if (oneDayBefore > System.currentTimeMillis()) {
                ReminderHelper.scheduleReminder(
                    context,
                    insertedId * 10 + 1,
                    oneDayBefore,
                    title,
                    "Panda Study says: '$title' is due tomorrow. You’ve got this!",
                    type
                )
            }
            if (oneHourBefore > System.currentTimeMillis()) {
                ReminderHelper.scheduleReminder(
                    context,
                    insertedId * 10 + 2,
                    oneHourBefore,
                    title,
                    "Urgent: '$title' is due in 1 hour!",
                    type
                )
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            val context = getApplication<Application>()
            
            ReminderHelper.cancelReminder(context, task.id * 10 + 1)
            ReminderHelper.cancelReminder(context, task.id * 10 + 2)

            if (!task.isCompleted) {
                val oneDayBefore = task.deadline - 86400_000L
                val oneHourBefore = task.deadline - 3600_000L
                val currentTime = System.currentTimeMillis()

                if (oneDayBefore > currentTime) {
                    ReminderHelper.scheduleReminder(
                        context,
                        task.id * 10 + 1,
                        oneDayBefore,
                        task.title,
                        "Panda Study says: '${task.title}' is due tomorrow. You’ve got this!",
                        task.type
                    )
                }
                if (oneHourBefore > currentTime) {
                    ReminderHelper.scheduleReminder(
                        context,
                        task.id * 10 + 2,
                        oneHourBefore,
                        task.title,
                        "Urgent: '${task.title}' is due in 1 hour!",
                        task.type
                    )
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            val context = getApplication<Application>()
            ReminderHelper.cancelReminder(context, task.id * 10 + 1)
            ReminderHelper.cancelReminder(context, task.id * 10 + 2)
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskCompletion(taskId, isCompleted)
            val context = getApplication<Application>()
            if (isCompleted) {
                ReminderHelper.cancelReminder(context, taskId * 10 + 1)
                ReminderHelper.cancelReminder(context, taskId * 10 + 2)
            }
        }
    }
}
