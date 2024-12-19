package com.lam.pedro.data.activity.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import androidx.health.connect.client.records.Record

suspend inline fun <reified T : Record> readFirstRecord(
    client: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): T? {
    return client.readRecords<T>(
        ReadRecordsRequest(
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records.firstOrNull()
}



