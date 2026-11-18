package com.example.mindora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.data.entity.Subject
import com.example.mindora.data.repository.SubjectRepository
import com.example.mindora.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubjectWithCount(
    val subject: Subject,
    val taskCount: Int
)

class SubjectViewModel(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val subjectsWithCount: StateFlow<List<SubjectWithCount>> = combine(
        subjectRepository.allSubjects,
        taskRepository.allTasksWithSubject
    ) { subjects, tasks ->
        subjects.map { subject ->
            val count = tasks.count { it.task.subjectId == subject.id }
            SubjectWithCount(subject = subject, taskCount = count)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addSubject(name: String, description: String, colorHex: String) {
        viewModelScope.launch {
            val subject = Subject(
                name = name,
                description = description,
                color = colorHex
            )
            subjectRepository.insertSubject(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subject)
        }
    }
}
