package com.lam.pedro.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lam.pedro.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val channelId = "periodic_notifications"
        val notificationId = 1

        // Creazione del canale di notifica (necessario per Android 8.0+)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Periodic Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Creazione della notifica
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Reminder")
            .setContentText("Remember to record an activity!")
            .setSmallIcon(R.drawable.pedro_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Invio della notifica
        notificationManager.notify(notificationId, notification)
    }
}


/*
class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        sendPeriodicNotification()

        val steps = getSteps() // Funzione che recupera i passi fatti

        // Verifica se i passi sono inferiori alla soglia e invia una notifica
        if (steps < 5000) { // Sostituisci con la soglia che preferisci
            sendReminderNotification()
        }

        return Result.success()
    }

    private fun sendPeriodicNotification() {
        val channelId = "periodic_notifications"
        val notificationId = 1

        // Creazione del canale di notifica (necessario per Android 8.0+)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Periodic Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Creazione della notifica
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Reminder")
            .setContentText("Remember to record an activity!")
            .setSmallIcon(R.drawable.pedro_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Invio della notifica
        notificationManager.notify(notificationId, notification)
    }

    private fun sendReminderNotification() {
        val channelId = "periodic_notifications"
        val notificationId = 2

        // Creazione della notifica che ricorda di fare più passi
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Move more!")
            .setContentText("You haven't done enough steps yet. Try to walk more!")
            .setSmallIcon(R.drawable.pedro_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Invio della notifica
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    // Funzione che recupera i passi (da implementare in base al tuo sistema di tracking)
    private fun getSteps(): Int {
        return 3000
    }
}

 */
