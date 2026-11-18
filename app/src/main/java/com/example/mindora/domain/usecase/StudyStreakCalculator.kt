package com.example.mindora.domain.usecase

import java.util.Calendar
import java.util.TimeZone

object StudyStreakCalculator {
    private const val MILLIS_PER_DAY = 86_400_000L

    /** Maps an absolute timestamp onto the civil date observed in the supplied timezone. */
    fun localEpochDay(timestampMillis: Long, timeZone: TimeZone): Long {
        val local = Calendar.getInstance(timeZone).apply { timeInMillis = timestampMillis }
        val year = local.get(Calendar.YEAR)
        val month = local.get(Calendar.MONTH)
        val day = local.get(Calendar.DAY_OF_MONTH)
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
            clear()
            set(year, month, day)
            timeInMillis / MILLIS_PER_DAY
        }
    }

    fun currentStreak(activityDays: Collection<Long>, todayEpochDay: Long): Int {
        val days = activityDays.toHashSet()
        val end = when {
            todayEpochDay in days -> todayEpochDay
            todayEpochDay - 1 in days -> todayEpochDay - 1
            else -> return 0
        }
        var length = 0
        var day = end
        while (day in days) {
            length++
            day--
        }
        return length
    }

    fun longestStreak(activityDays: Iterable<Long>): Int {
        val days = activityDays.toSet().sorted()
        var longest = 0
        var run = 0
        var previous: Long? = null
        for (day in days) {
            run = if (previous != null && day == previous + 1) run + 1 else 1
            longest = maxOf(longest, run)
            previous = day
        }
        return longest
    }

    /** Monday-first week values, where the input epoch days were recorded as local civil days. */
    fun weekActivity(activityDays: Collection<Long>, todayEpochDay: Long): List<Boolean> {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = todayEpochDay * MILLIS_PER_DAY
        }
        val mondayOffset = (utcCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekStart = todayEpochDay - mondayOffset
        val days = activityDays.toSet()
        return (0L..6L).map { weekStart + it in days }
    }
}
