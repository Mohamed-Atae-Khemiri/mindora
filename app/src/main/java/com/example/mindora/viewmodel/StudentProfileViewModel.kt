package com.example.mindora.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileFormErrors(
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val university: String? = null,
    val fieldOfStudy: String? = null,
    val academicYear: String? = null,
    val age: String? = null
)

data class StudentProfileUiState(
    val profile: StudentProfile? = null,
    val errors: ProfileFormErrors = ProfileFormErrors(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val saveError: String? = null
)

class StudentProfileViewModel(
    private val repository: StudentProfileRepository
) : ViewModel() {
    private val _state = kotlinx.coroutines.flow.MutableStateFlow(StudentProfileUiState())
    val state: StateFlow<StudentProfileUiState> = _state.asStateFlow()
    val profile: StateFlow<StudentProfile?> = repository.profile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    init {
        viewModelScope.launch {
            profile.collect { value -> _state.value = _state.value.copy(profile = value) }
        }
    }

    fun save(
        fullName: String,
        email: String,
        phoneNumber: String,
        university: String,
        fieldOfStudy: String,
        academicYear: String,
        avatarUri: String?,
        age: String = "",
        classGroup: String = ""
    ) {
        val errors = ProfileFormErrors(
            fullName = if (fullName.trim().length < 2) "Enter your full name." else null,
            email = if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Enter a valid email address." else null,
            phoneNumber = if (phoneNumber.isNotBlank() && phoneNumber.filter(Char::isDigit).length !in 7..15) {
                "Enter a valid phone number or leave this blank."
            } else null,
            university = if (university.isBlank()) "Enter your university." else null,
            fieldOfStudy = if (fieldOfStudy.isBlank()) "Enter your field of study." else null,
            academicYear = if (academicYear.isBlank()) "Choose your academic year." else null,
            age = if (age.isNotBlank() && age.toIntOrNull()?.let { it in 13..120 } != true) {
                "Enter an age from 13 to 120, or leave this blank."
            } else null
        )
        if (listOf(errors.fullName, errors.email, errors.phoneNumber, errors.university, errors.fieldOfStudy, errors.academicYear, errors.age).any { it != null }) {
            _state.value = _state.value.copy(errors = errors, saved = false, saveError = null)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(errors = ProfileFormErrors(), isSaving = true, saved = false, saveError = null)
            runCatching {
                repository.save(
                    StudentProfile(
                        fullName = fullName.trim(),
                        email = email.trim(),
                        phoneNumber = phoneNumber.trim(),
                        university = university.trim(),
                        fieldOfStudy = fieldOfStudy.trim(),
                        academicYear = academicYear.trim(),
                        avatarUri = avatarUri,
                        age = age.trim().takeIf(String::isNotEmpty)?.toInt(),
                        classGroup = classGroup.trim()
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(isSaving = false, saved = true)
            }.onFailure {
                _state.value = _state.value.copy(isSaving = false, saveError = "Your profile could not be saved. Please try again.")
            }
        }
    }

    fun clearSavedMessage() {
        _state.value = _state.value.copy(saved = false)
    }
}
