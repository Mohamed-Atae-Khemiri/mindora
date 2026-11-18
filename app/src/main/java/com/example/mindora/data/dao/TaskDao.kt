package com.example.mindora.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mindora.data.entity.Task
import com.example.mindora.data.entity.TaskWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId AND isCompleted != :isCompleted")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean): Int

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasksWithSubject(): Flow<List<TaskWithSubject>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskWithSubjectById(id: Long): Flow<TaskWithSubject?>

    @Transaction
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId ORDER BY deadline ASC")
    fun getTasksBySubject(subjectId: Long): Flow<List<TaskWithSubject>>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>
}
