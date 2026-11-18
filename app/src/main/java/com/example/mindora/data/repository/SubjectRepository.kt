package com.example.mindora.data.repository

import com.example.mindora.data.dao.SubjectDao
import com.example.mindora.data.entity.Subject
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {

    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

    fun getSubjectById(id: Long): Flow<Subject?> = subjectDao.getSubjectById(id)

    fun getTaskCountForSubject(subjectId: Long): Flow<Int> = subjectDao.getTaskCountForSubject(subjectId)

    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)

    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)

    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)
}
