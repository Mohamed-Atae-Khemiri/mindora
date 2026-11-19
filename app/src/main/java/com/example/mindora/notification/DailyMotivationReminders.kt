package com.example.mindora.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mindora.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

object DailyMotivationReminders {
    private const val PREFS = "mindora_preferences"
    private const val ENABLED = "motivation_enabled"
    private const val NAME = "motivation_student_name"
    private const val ACTION = "com.example.mindora.ACTION_DAILY_MOTIVATION"
    private const val EXTRA_SLOT = "motivation_slot"
    private const val CHANNEL_ID = "mindora_reminders_channel"
    private val defaultMinutes = intArrayOf(8 * 60, 14 * 60, 20 * 60)
    private val slotNames = arrayOf("morning", "afternoon", "evening")
    private val slotTitles = arrayOf("A fresh start", "Study break", "Today’s progress")
    private val messages = arrayOf(
        arrayOf(
            "Good morning, %s! Choose one important task and take the first small step.",
            "%s, your goals are within reach. Start today with one focused study session.",
            "A new day, %s! Make progress on a task before the day gets busy."
        ),
        arrayOf(
            "%s, it’s a great time for a focused study sprint. Every session moves you forward.",
            "Take 25 minutes to study, %s. Your daily goals are built one session at a time.",
            "%s, pick one task and give it your attention. You’re making steady progress."
        ),
        arrayOf(
            "Well done today, %s. What is one task you can finish before you wrap up?",
            "%s, take a moment to notice what you achieved today and choose tomorrow’s first step.",
            "Your effort counts, %s. Check off a final task or celebrate the progress you made."
        )
    )

    fun isEnabled(context: Context) = prefs(context).getBoolean(ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(ENABLED, enabled).apply()
        if (enabled) rescheduleAll(context) else cancelAll(context)
    }

    fun getMinutes(context: Context, slot: Int): Int =
        prefs(context).getInt(timeKey(slot), defaultMinutes[slot])

    fun saveTime(context: Context, slot: Int, minutes: Int) {
        prefs(context).edit().putInt(timeKey(slot), minutes).apply()
        if (isEnabled(context)) schedule(context, slot)
    }

    fun saveStudentName(context: Context, name: String) {
        prefs(context).edit().putString(NAME, name.trim()).apply()
    }

    fun rescheduleAll(context: Context) {
        if (!isEnabled(context)) return
        repeat(3) { schedule(context, it) }
    }

    fun cancelAll(context: Context) {
        repeat(3) { slot ->
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent(context, slot))
        }
    }

    fun deliverAndReschedule(context: Context, slot: Int) {
        val preferences = prefs(context)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastDelivered = preferences.getString(lastDateKey(slot), null)
        val now = Calendar.getInstance()
        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val quietHours = nowMinutes >= 21 * 60 || nowMinutes < 7 * 60
        val notificationsAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED)

        if (isEnabled(context) && lastDelivered != today && !quietHours && notificationsAllowed) {
            val name = preferences.getString(NAME, null)?.takeIf { it.isNotBlank() } ?: "student"
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val text = messages[slot][(dayOfYear + slot) % messages[slot].size].format(name)
            ReminderHelper.createNotificationChannel(context)
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentIntent = PendingIntent.getActivity(
                context, 8700 + slot, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(slotTitles[slot])
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build()
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(8700 + slot, notification)
            preferences.edit().putString(lastDateKey(slot), today).apply()
        }
        if (isEnabled(context)) schedule(context, slot)
    }

    private fun schedule(context: Context, slot: Int) {
        val minutes = getMinutes(context, slot)
        val hour = minutes / 60
        val minute = minutes % 60
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }
        // Quiet hours are 21:00–07:00; never schedule a reminder in that window.
        if (hour < 7 || hour >= 21) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger.timeInMillis, pendingIntent(context, slot))
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger.timeInMillis, pendingIntent(context, slot))
        }
    }

    private fun pendingIntent(context: Context, slot: Int): PendingIntent {
        val intent = Intent(context, MotivationReminderReceiver::class.java).apply {
            action = ACTION
            putExtra(EXTRA_SLOT, slot)
        }
        return PendingIntent.getBroadcast(
            context, 8600 + slot, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private fun timeKey(slot: Int) = "motivation_time_${slotNames[slot]}"
    private fun lastDateKey(slot: Int) = "motivation_last_date_${slotNames[slot]}"
}
