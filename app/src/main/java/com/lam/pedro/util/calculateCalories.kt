package com.lam.pedro.util

/*
* This function calculates the active calories burned by the user based on
* 3 different methods returning the average value and using it to calculate
* the total calories burned value
* */
fun calculateCalories(
    weight: Double,
    height: Double,
    age: Int,
    sex: String,
    distance: Double,
    steps: Int,
    durationInMinutes: Long,
    averageSpeed: Double // in m/s
): Pair<Double, Double> {
    // Calcolo del BMR
    val bmr = if (sex == "male") {
        88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
    } else {
        447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
    }

    // 1. MET Method
    val met = when {
        averageSpeed <= 2.2 -> 3.5 // Slow walk
        averageSpeed <= 3.6 -> 5.0 // Fast walk
        averageSpeed <= 5.6 -> 8.3 // Slow run
        else -> 11.5 // Fast run
    }
    val activeCaloriesMet = met * weight * (durationInMinutes / 60.0)

    // 2. Distance method
    val caloriesByDistance = distance * weight * if (averageSpeed < 3.0) 0.035 else 0.045

    // 3. Steps method
    val caloriesBySteps = steps * weight * if (averageSpeed < 3.0) 0.04 else 0.06

    // Active calories mean
    val activeCalories = (activeCaloriesMet + caloriesByDistance + caloriesBySteps) / 3.0

    // Total calories using BMR
    val totalCalories = (bmr / 1440.0) * durationInMinutes + activeCalories

    return Pair(totalCalories, activeCalories)
}
