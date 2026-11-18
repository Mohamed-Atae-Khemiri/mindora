package com.example.mindora.domain.usecase

import com.example.mindora.domain.model.SmartPriorityLabel
import java.util.concurrent.TimeUnit

object CalculateSmartPriority {
    /**
     * Classifies a task using completion state, deadline, and its user-selected priority.
     */
    fun invoke(
        isCompleted: Boolean,
        deadlineMillis: Long,
        priority: String,
        nowMillis: Long = System.currentTimeMillis()
    ): SmartPriorityLabel {
        if (isCompleted) return SmartPriorityLabel.COMPLETED

        val daysUntilDeadline = TimeUnit.MILLISECONDS.toDays(deadlineMillis - nowMillis)
        return when {
            daysUntilDeadline <= 1 || (priority == "HIGH" && daysUntilDeadline <= 2) ->
                SmartPriorityLabel.URGENT
            daysUntilDeadline <= 3 || priority == "HIGH" ->
                SmartPriorityLabel.UPCOMING
            else -> SmartPriorityLabel.LOW_PRIORITY
        }
    }
}
