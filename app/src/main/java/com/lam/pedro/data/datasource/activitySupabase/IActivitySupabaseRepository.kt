package com.lam.pedro.data.datasource.activitySupabase

import android.net.Uri
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity

interface IActivitySupabaseRepository {
    suspend fun getActivitySession(activityEnum: ActivityEnum, uuid: String): List<GenericActivity>
    suspend fun insertActivitySession(activity: GenericActivity, userUUID: String)
    suspend fun insertActivitySession(activities: List<GenericActivity>, userUUID: String)
    suspend fun exportDataFromDB(uuid: String): String
    suspend fun importJsonToDatabase(uri: Uri, userUUID: String)
}