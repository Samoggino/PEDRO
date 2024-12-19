package com.lam.pedro.data

import android.util.Log
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.Instant

suspend fun fetchFromHealthConnectForDB(healthConnectManager: HealthConnectManager): List<GenericActivity> {

    val start = Instant.EPOCH // 1st January 1970
    val now = Instant.now()


    val allActivities = coroutineScope {
        ActivityEnum.entries.map {
            async { healthConnectManager.fetchAndBuildActivitySession(start, now, it.activityType) }
        }.awaitAll().flatten()
    }

    Log.d("fetchFromHealthConnectForDB", "allActivities: $allActivities")


    return allActivities


}