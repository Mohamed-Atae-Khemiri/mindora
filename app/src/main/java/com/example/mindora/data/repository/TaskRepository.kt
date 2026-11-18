package com.example.mindora.data.repository

import com.example.mindora.data.dao.TaskDao
import com.example.mindora.data.entity.Task
import com.example.mindora.data.entity.TaskWithSubject
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val studyActivityRepository: StudyActivityRepository
) {

    val allTasksWithSubject: Flow<List<TaskWithSubject>> = taskDao.getAllTasksWithSubject()

    val totalTaskCount: Flow<Int> = taskDao.getTotalTaskCount()

    val completedTaskCount: Flow<Int> = taskDao.getCompletedTaskCount()

    val pendingTaskCount: Flow<Int> = taskDao.getPendingTaskCount()

    fun getTaskById(id: Long): Flow<TaskWithSubject?> = taskDao.getTaskWithSubjectById(id)

    fun getTasksBySubject(subjectId: Long): Flow<List<TaskWithSubject>> = taskDao.getTasksBySubject(subjectId)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val changedRows = taskDao.updateTaskCompletion(taskId, isCompleted)
        if (isCompleted && changedRows > 0) {
            studyActivityRepository.recordCompletedStudyActivity(
                source = "task_completion",
                sourceId = taskId.toString()
            )
        }
    }
}
