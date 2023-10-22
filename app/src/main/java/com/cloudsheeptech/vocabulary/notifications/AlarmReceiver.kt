package com.cloudsheeptech.vocabulary.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.cloudsheeptech.vocabulary.MainActivity
import com.cloudsheeptech.vocabulary.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendReminderNotification(
            context,
            context.getString(R.string.CHANNEL_ID)
        )
    }
}

const val NOTIFICATION_ID = 1
fun NotificationManager.sendReminderNotification(applicationContext : Context, channelId : String) {
    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Add this extra information to let the main activity know that we want to navigate to the recap fragment because we are coming from a notification
        putExtra("redirect", "recapFragment")
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.ic_recap)
        .setContentTitle("Start a new recap")
        .setContentText("Tab here to start your daily recap!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}