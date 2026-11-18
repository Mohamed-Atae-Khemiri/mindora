package com.example.mindora.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mindora.data.database.MindoraDatabase
import com.example.mindora.data.repository.StudyActivityRepository
import com.example.mindora.data.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                val db = MindoraDatabase.getDatabase(context)
                val studyActivityRepository = StudyActivityRepository(db.studyActivityDao())
                val taskRepository = TaskRepository(db.taskDao(), studyActivityRepository)
                val tasksWithSubject = taskRepository.allTasksWithSubjectSnapshot()
                val currentTime = System.currentTimeMillis()

                for (item in tasksWithSubject) {
                    val task = item.task
                    if (!task.isCompleted && task.deadline > currentTime) {
                        val oneHourBefore = task.deadline - 3600_000L
                        val oneDayBefore = task.deadline - 86400_000L

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
        }
    }
}
