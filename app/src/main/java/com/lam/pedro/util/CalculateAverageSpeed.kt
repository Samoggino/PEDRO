package com.lam.pedro.util

import androidx.health.connect.client.records.SpeedRecord

fun calculateAverageSpeed(samples: List<SpeedRecord.Sample>): Double {
    // Verifica se la lista è vuota per evitare divisione per zero
    if (samples.isEmpty()) return 0.0

    // Calcola la somma delle velocità manualmente
    var totalSpeed = 0.0
    for (sample in samples) {
        totalSpeed += sample.speed.inMetersPerSecond
    }

    // Dividi per il numero di campioni per ottenere la velocità media
    return totalSpeed / samples.size
}