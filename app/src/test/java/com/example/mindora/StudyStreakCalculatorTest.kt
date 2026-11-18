package com.example.mindora

import com.example.mindora.domain.usecase.StudyStreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class StudyStreakCalculatorTest {
    @Test
    fun currentStreakCountsConsecutiveDaysIncludingToday() {
        assertEquals(3, StudyStreakCalculator.currentStreak(listOf(40, 41, 42), 42))
    }

    @Test
    fun currentStreakAllowsTodayToRemainUncompletedUntilEndOfDay() {
        assertEquals(2, StudyStreakCalculator.currentStreak(listOf(40, 41), 42))
    }

    @Test
    fun missedDayBreaksCurrentStreak() {
        assertEquals(0, StudyStreakCalculator.currentStreak(listOf(40, 41), 43))
    }

    @Test
    fun duplicateEventsOnOneDayDoNotInflateStreak() {
        assertEquals(2, StudyStreakCalculator.currentStreak(listOf(41, 41, 41, 42), 42))
        assertEquals(3, StudyStreakCalculator.longestStreak(listOf(1, 1, 2, 10, 11, 12, 12)))
    }

    @Test
    fun localDayUsesTheTimezoneAtActivityTime() {
        val timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.MARCH, 29, 23, 30, 0)
        }.timeInMillis
        val utcDay = StudyStreakCalculator.localEpochDay(timestamp, TimeZone.getTimeZone("UTC"))
        val tunisDay = StudyStreakCalculator.localEpochDay(timestamp, TimeZone.getTimeZone("Africa/Tunis"))
        assertEquals(utcDay + 1, tunisDay)
    }

    @Test
    fun timezoneChangeUsesRecordedCivilDaysWithoutAddingActivity() {
        val days = listOf(100L, 101L, 102L)
        assertEquals(3, StudyStreakCalculator.currentStreak(days, 102L))
        assertEquals(0, StudyStreakCalculator.currentStreak(days, 104L))
    }

    @Test
    fun weeklyActivityIsMondayFirstAndIgnoresDuplicateDays() {
        // 2026-11-17 is Tuesday (epoch day 20774).
        val week = StudyStreakCalculator.weekActivity(listOf(20773, 20774, 20774), 20774)
        assertEquals(listOf(true, true, false, false, false, false, false), week)
    }
}
