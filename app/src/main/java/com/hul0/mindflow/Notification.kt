package com.hul0.mindflow

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

fun showNotification(context: Context, title: String, message: String) {
    val notification = NotificationCompat.Builder(context, "mindflow_channel")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(message)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(System.currentTimeMillis().toInt(), notification)
}