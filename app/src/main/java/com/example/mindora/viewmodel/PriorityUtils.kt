package com.example.mindora.viewmodel

import com.example.mindora.data.entity.Task
import com.example.mindora.ui.components.SmartPriorityLabel
import java.util.concurrent.TimeUnit

object PriorityUtils {

    /**
     * Calculates automatic smart priority label based on task completion, deadline, and user priority.
     *
     * Rules:
     * 1. Completed tasks are NEVER classified as urgent -> Returns COMPLETED.
     * 2. Tasks due within 1 day (or High priority due within 2 days) -> Returns URGENT.
     * 3. Tasks due within 3 days or marked as HIGH priority -> Returns UPCOMING.
     * 4. Otherwise -> Returns LOW_PRIORITY.
     */
    fun calculateSmartPriority(task: Task): SmartPriorityLabel {
        if (task.isCompleted) {
            return SmartPriorityLabel.COMPLETED
        }

        val currentTimeMillis = System.currentTimeMillis()
        val diffMillis = task.deadline - currentTimeMillis
        val daysUntilDeadline = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            daysUntilDeadline <= 1 || (task.priority == "HIGH" && daysUntilDeadline <= 2) -> {
                SmartPriorityLabel.URGENT
            }
            daysUntilDeadline <= 3 || task.priority == "HIGH" -> {
                SmartPriorityLabel.UPCOMING
            }
            else -> {
                SmartPriorityLabel.LOW_PRIORITY
            }
        }
    }
}
