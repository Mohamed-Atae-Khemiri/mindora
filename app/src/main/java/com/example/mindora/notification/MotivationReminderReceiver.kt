package com.example.mindora.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MotivationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val slot = intent.getIntExtra("motivation_slot", -1)
        if (slot in 0..2) DailyMotivationReminders.deliverAndReschedule(context, slot)
    }
}
