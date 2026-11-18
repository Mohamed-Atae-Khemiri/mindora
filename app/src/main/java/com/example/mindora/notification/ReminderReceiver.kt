package com.example.mindora.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mindora.data.database.MindoraDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(ReminderHelper.EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        if (intent.action == ReminderHelper.ACTION_COMPLETE_TASK) {
            // Mark task as completed in Room database
            CoroutineScope(Dispatchers.IO).launch {
                val db = MindoraDatabase.getDatabase(context)
                db.taskDao().updateTaskCompletion(taskId, true)
                ReminderHelper.cancelReminder(context, taskId)
            }
        } else {
            val title = intent.getStringExtra("TITLE") ?: "Task Reminder"
            val message = intent.getStringExtra("MESSAGE") ?: "You have a deadline approaching."
            val taskType = intent.getStringExtra("TYPE") ?: "ASSIGNMENT"

            ReminderHelper.showNotification(context, taskId, title, message, taskType)
        }
    }
}
