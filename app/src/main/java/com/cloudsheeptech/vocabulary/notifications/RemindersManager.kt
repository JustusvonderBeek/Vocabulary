package com.cloudsheeptech.vocabulary.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.cloudsheeptech.vocabulary.MainActivity
import java.util.Calendar
import java.util.Locale

class RemindersManager {

    companion object {

        const val REMINDER_NOTIFICATION_REQUEST_CODE = 123

        fun startReminder(
            context: Context,
            reminderTime: String = "18:32",
            reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val (hours, min) = reminderTime.split(":").map { it.toInt() }
            val intent =
                Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            val calendar: Calendar = Calendar.getInstance(Locale.GERMAN).apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, min)
            }
            // Trigger the notification next day
            if (Calendar.getInstance(Locale.GERMAN)
                    .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
            ) {
                calendar.add(Calendar.DATE, 1)
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
//                alarmManager.setExact(AlarmManager.RTC,
//                    calendar.timeInMillis,
//                    intent
//                )
//            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, intent)
                Log.i("RemindersManager", "Scheduled time window for reminder at ${calendar.time}")
//            }
        }

        fun stopReminder(context: Context, reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE) {
            val alarmManger = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
            alarmManger.cancel(intent)
        }
    }

}