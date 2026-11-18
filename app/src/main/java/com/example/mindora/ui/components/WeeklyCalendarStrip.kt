package com.example.mindora.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.RichViolet
import com.example.mindora.ui.theme.TextMuted
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val dayOfWeek: String,
    val dayOfMonth: String,
    val isToday: Boolean,
    val date: Date
)

@Composable
fun WeeklyCalendarStrip(
    modifier: Modifier = Modifier,
    onDaySelected: (Date) -> Unit = {}
) {
    val days = remember { getWeekDays() }
    var selectedIndex by remember { mutableIntStateOf(days.indexOfFirst { it.isToday }.coerceAtLeast(0)) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("THIS WEEK", fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, color = TextMuted)
            Text(
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(days[selectedIndex].date),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            days.forEachIndexed { index, day ->
                val selected = index == selectedIndex
                val circleColor by animateColorAsState(
                    targetValue = if (selected) RichViolet else Color.Transparent,
                    animationSpec = tween(180),
                    label = "calendarDaySelection"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable {
                            selectedIndex = index
                            onDaySelected(day.date)
                        }
                        .padding(vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        day.dayOfWeek,
                        fontSize = 10.sp,
                        fontWeight = if (selected || day.isToday) FontWeight.Bold else FontWeight.Medium,
                        color = if (day.isToday && !selected) CoralAccent else TextMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(circleColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day.dayOfMonth,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(if (day.isToday && !selected) CoralAccent else Color.Transparent)
                    )
                }
            }
        }
    }
}

private fun getWeekDays(): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    val calendar = Calendar.getInstance()
    val todayDate = calendar.time
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val numFormat = SimpleDateFormat("dd", Locale.getDefault())
    val sameDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val todayKey = sameDateFormat.format(todayDate)

    for (i in 0..6) {
        val date = calendar.time
        days.add(
            CalendarDay(
                dayOfWeek = dayFormat.format(date).take(3).uppercase(),
                dayOfMonth = numFormat.format(date),
                isToday = sameDateFormat.format(date) == todayKey,
                date = date
            )
        )
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}
