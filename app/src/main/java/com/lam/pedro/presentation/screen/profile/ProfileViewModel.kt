package com.lam.pedro.presentation.screen.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lam.pedro.presentation.TAG

// ViewModel per centralizzare i dati del profilo
class ProfileViewModel(context: Context) : ViewModel() {

    private val sharedPreferences =
        context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    // Stato centralizzato per i dati del profilo
    var firstName by mutableStateOf(
        sharedPreferences.getString("firstName", "FirstName") ?: "FirstName"
    )
        private set
    var lastName by mutableStateOf(
        sharedPreferences.getString("lastName", "SecondName") ?: "SecondName"
    )
        private set
    var age by mutableStateOf(sharedPreferences.getString("age", "Age") ?: "Age")
        private set
    var sex by mutableStateOf(sharedPreferences.getString("sex", "Sex") ?: "Sex")
        private set
    var weight by mutableStateOf(sharedPreferences.getString("weight", "Weight") ?: "Weight")
        private set
    var height by mutableStateOf(sharedPreferences.getString("height", "Height") ?: "Height")
        private set
    var nationality by mutableStateOf(
        sharedPreferences.getString("nationality", "Nationality") ?: "Nationality"
    )
        private set


    // Funzioni per aggiornare i dati
    fun updateFirstName(value: String) {
        firstName = value
        editor.putString("firstName", value).apply()
    }

    fun updateLastName(value: String) {
        lastName = value
        editor.putString("lastName", value).apply()
    }

    fun updateAge(value: String) {
        age = value
        editor.putString("age", value).apply()
    }

    fun updateSex(value: String) {
        sex = value
        editor.putString("sex", value).apply()
    }

    fun updateWeight(value: String) {
        weight = value
        editor.putString("weight", value).apply()
    }

    fun updateHeight(value: String) {
        height = value
        editor.putString("height", value).apply()
    }

    fun updateNationality(value: String) {
        nationality = value
        editor.putString("nationality", value).apply()
    }


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
            .commit()
    }

    fun reloadProfileData() {
        firstName = sharedPreferences.getString("firstName", "FirstName") ?: "FirstName"
        Log.d(TAG, "reloadProfileData: $firstName")
        lastName = sharedPreferences.getString("lastName", "SecondName") ?: "SecondName"
        Log.d(TAG, "reloadProfileData: $lastName")
        age = sharedPreferences.getString("age", "Age") ?: "Age"
        Log.d(TAG, "reloadProfileData: $age")
        sex = sharedPreferences.getString("sex", "Sex") ?: "Sex"
        Log.d(TAG, "reloadProfileData: $sex")
        weight = sharedPreferences.getString("weight", "Weight") ?: "Weight"
        Log.d(TAG, "reloadProfileData: $weight")
        height = sharedPreferences.getString("height", "Height") ?: "Height"
        Log.d(TAG, "reloadProfileData: $height")
        nationality = sharedPreferences.getString("nationality", "Nationality") ?: "Nationality"
        Log.d(TAG, "reloadProfileData: $nationality")
    }

}
