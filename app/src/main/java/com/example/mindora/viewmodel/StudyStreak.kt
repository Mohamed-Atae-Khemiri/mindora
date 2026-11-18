package com.example.mindora.viewmodel

import com.example.mindora.data.streak.StudyStreakCalculator

data class StudyStreakState(
    val currentDays: Int = 0,
    val bestDays: Int = 0,
    val activeThisWeek: List<Boolean> = List(7) { false },
    val weeklyActiveDays: Int = 0
)

fun buildStudyStreakState(days: List<Long>, today: Long): StudyStreakState {
    val uniqueDays = days.toSet()
    val week = StudyStreakCalculator.weekActivity(uniqueDays, today)
    return StudyStreakState(
        currentDays = StudyStreakCalculator.currentStreak(uniqueDays, today),
        bestDays = StudyStreakCalculator.longestStreak(uniqueDays),
        activeThisWeek = week,
        weeklyActiveDays = week.count { it }
    )
}
