package com.lam.pedro.util.notification

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun schedulePeriodicNotifications(context: Context, intervalMinutes: Long) {
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        intervalMinutes, TimeUnit.MINUTES
    )
        .addTag("periodic_notification")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "PeriodicNotificationWork",
        androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

fun cancelPeriodicNotifications(context: Context) {
    WorkManager.getInstance(context).cancelAllWorkByTag("periodic_notification")
}

fun areNotificationsActive(context: Context, callback: (Boolean) -> Unit) {
    WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData("PeriodicNotificationWork")
        .observeForever { workInfos ->
            val isActive = workInfos.any { it.state == WorkInfo.State.ENQUEUED }
            callback(isActive)
        }
}
