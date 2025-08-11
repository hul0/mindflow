package com.hul0.mindflow.ui.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hul0.mindflow.NotificationWorker
import java.util.concurrent.TimeUnit

class NotificationViewModel(private val application: Application) : ViewModel() {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MindFlow Reminders"
            val descriptionText = "Notifications for MindFlow"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("mindflow_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(10, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(application).enqueue(workRequest)
    }
}