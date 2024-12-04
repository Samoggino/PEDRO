package com.lam.pedro.presentation.screen.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// ViewModel per centralizzare i dati del profilo
class ProfileViewModel(context: Context) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

    // Stato centralizzato per i dati del profilo
    var firstName by mutableStateOf(sharedPreferences.getString("firstName", "Angela") ?: "Angela")
        private set
    var lastName by mutableStateOf(sharedPreferences.getString("lastName", "Taylor") ?: "Taylor")
        private set
    var age by mutableStateOf(sharedPreferences.getString("age", "27") ?: "27")
        private set
    var sex by mutableStateOf(sharedPreferences.getString("sex", "male") ?: "male")
        private set
    var weight by mutableStateOf(sharedPreferences.getString("weight", "70.5") ?: "70.5")
        private set
    var height by mutableStateOf(sharedPreferences.getString("height", "1.75") ?: "1.75")
        private set
    var nationality by mutableStateOf(sharedPreferences.getString("nationality", "American") ?: "American")
        private set

    // Metodo per aggiornare i valori e salvarli in SharedPreferences
    fun updateProfileField(key: String, value: String) {
        when (key) {
            "firstName" -> firstName = value
            "lastName" -> lastName = value
            "sex" -> sex = value
            "age" -> age = value
            "weight" -> weight = value
            "height" -> height = value
            "nationality" -> nationality = value
        }
        saveToPreferences(key, value)
    }

    private fun saveToPreferences(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }
}