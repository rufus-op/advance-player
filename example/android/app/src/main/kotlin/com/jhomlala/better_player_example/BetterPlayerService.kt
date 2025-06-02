package com.jhomlala.better_player_example

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN

class BetterPlayerService : Service() {

    companion object {
        const val notificationId = 20772077
        const val foregroundNotificationId = 20772078
        const val channelId = "VideoPlayer"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(channelId, "Channel")
            } else {
                ""
            }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Build the initial notification
        val initialNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Better Player Notification")
            .setContentText("Better Player is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initialNotification.setCategory(Notification.CATEGORY_SERVICE);
        }
        startForeground(foregroundNotificationId, initialNotification.build())

        // Example of updating the notification later (e.g., in response to an event)
        // You would call this from within your service's logic, not directly in onStartCommand
        fun updateNotification(newText: String) {
            val updatedNotification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Better Player Notification")
                .setContentText(newText) // Use the new text
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updatedNotification.setCategory(Notification.CATEGORY_SERVICE)
            }
            notificationManager.notify(foregroundNotificationId, updatedNotification.build()) // Use the same ID
        }
        if (intent != null) {
            when (intent.action) {
                "ACTION_PLAY" -> {
                    // Start playing media
                    updateNotification("Playing...")
                }

                "ACTION_PAUSE" -> {
                    // Pause media
                    updateNotification("Paused")
                }

                "ACTION_SEEK" -> {
                    val position = intent.getIntExtra("position", 0)
                    // Seek to the specified position
                    updateNotification("Seeking to $position")
                }

                "ACTION_STOP" -> {
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    stopSelf();
                }

                else -> {
                    // Handle default case or unknown action
                }
            }
        }
        // Example usage:
        // updateNotification("Playing: Some Video"); // Call this when the video starts playing
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            val notificationManager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager
            notificationManager.cancel(notificationId)
        } catch (exception: Exception) {

        } finally {
            stopSelf()
        }
    }

}