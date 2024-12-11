package com.lam.pedro.util

fun calculateCalories(
    weight: Double, // in kg
    height: Double, // in cm
    age: Int, // in anni
    sex: String, // "male" o "female"
    distance: Double, // in metri
    steps: Int, // numero di passi
    duration: Long, // in minuti
    averageSpeed: Double // in m/s
): Pair<Double, Double> { // Restituisce (calorieTotali, calorieAttive)
    // Calcola il MET base in base alla velocità media
    val met = when {
        averageSpeed <= 2.2 -> 3.5 // Camminata lenta
        averageSpeed <= 3.6 -> 5.0 // Camminata veloce
        averageSpeed <= 5.6 -> 8.3 // Corsa leggera (~8 km/h)
        else -> 11.5 // Corsa intensa (~12 km/h)
    }

    // Calcola il BMR (Metabolismo Basale)
    val bmr = if (sex.lowercase() == "male") {
        88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age)
    } else {
        447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age)
    }

    // Calcola calorie per passo in base all'intensità (MET)
    val caloriesPerStep = met * (weight / 2000)

    // Calcola calorie dai passi
    val caloriesFromSteps = steps * caloriesPerStep

    // Converti la durata in ore
    val durationInHours = duration / 60.0

    // Calcola le calorie totali con MET (basate su peso e durata)
    val totalCaloriesFromMET = met * weight * durationInHours

    // Fai una media ponderata tra calorie basate su passi e su MET/distanza
    val totalCalories = 0.6 * totalCaloriesFromMET + 0.4 * caloriesFromSteps

    // Calcola le calorie attive sottraendo il metabolismo basale (BMR)
    val activeCalories = totalCalories - (bmr * durationInHours)

    return Pair(totalCalories, activeCalories)
} //FIXME: usa durationInMinutes

