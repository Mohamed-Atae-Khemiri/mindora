package com.example.mindora.data.repository

import com.example.mindora.data.dao.StudentProfileDao
import com.example.mindora.data.entity.StudentProfile
import kotlinx.coroutines.flow.Flow

class StudentProfileRepository(private val dao: StudentProfileDao) {
    val profile: Flow<StudentProfile?> = dao.observeProfile()
    suspend fun save(profile: StudentProfile) = dao.saveProfile(profile)
}
