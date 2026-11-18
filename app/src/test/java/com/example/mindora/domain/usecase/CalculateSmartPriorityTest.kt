package com.example.mindora.domain.usecase

import com.example.mindora.domain.model.SmartPriorityLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSmartPriorityTest {
    private val now = 1_000_000L
    private val day = 86_400_000L

    @Test
    fun completedTaskAlwaysHasCompletedPriority() {
        assertEquals(
            SmartPriorityLabel.COMPLETED,
            CalculateSmartPriority.invoke(true, now - day, "HIGH", now)
        )
    }

    @Test
    fun nearDeadlineIsUrgentAndThreeDayDeadlineIsUpcoming() {
        assertEquals(
            SmartPriorityLabel.URGENT,
            CalculateSmartPriority.invoke(false, now + day, "MEDIUM", now)
        )
        assertEquals(
            SmartPriorityLabel.UPCOMING,
            CalculateSmartPriority.invoke(false, now + 3 * day, "MEDIUM", now)
        )
    }

    @Test
    fun highPriorityTaskDueWithinTwoDaysIsUrgent() {
        assertEquals(
            SmartPriorityLabel.URGENT,
            CalculateSmartPriority.invoke(false, now + 2 * day, "HIGH", now)
        )
    }
}
