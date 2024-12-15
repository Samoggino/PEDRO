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

fun calculateCyclingCalories(
    weight: Double,            // Peso in kg
    height: Double,            // Altezza in cm (usata solo per BMR)
    age: Int,                  // Età
    sex: String,               // Sesso ("male" o "female")
    distance: Double,          // Distanza percorsa in km
    durationInMinutes: Long,   // Durata in minuti
    averageSpeed: Double       // Velocità media in m/s
): Pair<Double, Double> {
    // Calcolo del BMR
    val bmr = if (sex == "male") {
        88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
    } else {
        447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
    }

    // 1. MET Method
    val met = when {
        averageSpeed <= 4.4 -> 4.0  // Leisure cycling (e.g., slow pace)
        averageSpeed <= 6.7 -> 6.8  // Moderate cycling
        averageSpeed <= 8.9 -> 8.0  // Vigorous cycling
        else -> 10.0 // Racing or very fast cycling
    }
    val activeCaloriesMet = met * weight * (durationInMinutes / 60.0)

    // 2. Distance method
    val caloriesByDistance = distance * weight * if (averageSpeed < 5.5) 0.035 else 0.045

    // Active calories mean
    val activeCalories = (activeCaloriesMet + caloriesByDistance) / 2.0

    // Total calories using BMR
    val totalCalories = (bmr / 1440.0) * durationInMinutes + activeCalories

    return Pair(totalCalories, activeCalories)
}

fun calculateTrainCalories(
    weight: Double,            // Peso in kg
    height: Double,            // Altezza in cm (usata solo per BMR)
    age: Int,                  // Età
    sex: String,               // Sesso ("male" o "female")
    durationInMinutes: Long,   // Durata in minuti
    intensityLevel: String     // Livello di intensità: "moderate" o "vigorous"
): Pair<Double, Double> {
    // Calcolo del BMR
    val bmr = if (sex == "male") {
        88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
    } else {
        447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
    }

    // 1. MET Method (intensità dell'allenamento)
    val met = when (intensityLevel.lowercase()) {
        "moderate" -> 4.5 // Allenamento moderato (es. pesi leggeri)
        "vigorous" -> 6.0 // Allenamento vigoroso (es. sollevamento pesi pesanti)
        else -> 3.5 // Default: intensità leggera
    }
    val activeCalories = met * weight * (durationInMinutes / 60.0)

    // Total calories using BMR
    val totalCalories = (bmr / 1440.0) * durationInMinutes + activeCalories

    return Pair(totalCalories, activeCalories)
}

fun calculateYogaCalories(
    weight: Double,            // Peso in kg
    height: Double,            // Altezza in cm (usata solo per BMR)
    age: Int,                  // Età
    sex: String,               // Sesso ("male" o "female")
    durationInMinutes: Long,   // Durata in minuti
    yogaStyle: String          // Stile di yoga: "gentle", "moderate", "vigorous"
): Pair<Double, Double> {
    // Calcolo del BMR
    val bmr = if (sex == "male") {
        88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
    } else {
        447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
    }

    // 1. MET Method (stile di yoga)
    val met = when (yogaStyle.lowercase()) {
        "gentle" -> 2.5 // Yoga leggero (es. Yin yoga)
        "moderate" -> 3.5 // Yoga moderato (es. Hatha yoga)
        "vigorous" -> 5.0 // Yoga vigoroso (es. Vinyasa yoga)
        else -> 2.5 // Default: yoga leggero
    }
    val activeCalories = met * weight * (durationInMinutes / 60.0)

    // Total calories using BMR
    val totalCalories = (bmr / 1440.0) * durationInMinutes + activeCalories

    return Pair(totalCalories, activeCalories)
}
