package com.praneet.clarity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.praneet.clarity.MainActivity
import com.praneet.clarity.R

class FocusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val channelId = "focus_channel"
        val notificationId = 101

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //GET REAL DATA FROM INTENT
        val duration = intent.getIntExtra("duration", 25)
        val goalId = intent.getStringExtra("goalId") ?: "general"

        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        //SAVE SESSION
        if (userId != null) {
            val session = hashMapOf(
                "userId" to userId,
                "goalId" to goalId,
                "duration" to duration,
                "energy" to "AUTO",
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            firestore.collection("sessions").add(session)
        }

        //UPDATE GOAL PROGRESS
        if (userId != null && goalId != "general") {
            val goalRef = firestore.collection("users")
                .document(userId)
                .collection("goals")
                .document(goalId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(goalRef)
                val currentMinutes = snapshot.getLong("currentMinutes") ?: 0L
                transaction.update(goalRef, "currentMinutes", currentMinutes + duration)
            }
        }

        //NOTIFICATION CHANNEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val channel = NotificationChannel(
                channelId,
                "Focus Session Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when your focus timer finishes"
                setSound(alarmSound, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        //OPEN APP ON CLICK
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //BUILD NOTIFICATION
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Clarity ✨")
            .setContentText("Focus session complete! Great job!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setOngoing(true) // Makes it harder to dismiss without seeing
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        //Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationId, builder.build())
            }
        } else {
            notificationManager.notify(notificationId, builder.build())
        }
    }
}