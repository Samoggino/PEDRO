package com.lam.pedro.presentation.screen.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lam.pedro.data.datasource.SecurePreferencesManager.getProfileData
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveProfileData
import com.lam.pedro.presentation.screen.profile.ProfilePreference.AGE
import com.lam.pedro.presentation.screen.profile.ProfilePreference.FIRST_NAME
import com.lam.pedro.presentation.screen.profile.ProfilePreference.HEIGHT
import com.lam.pedro.presentation.screen.profile.ProfilePreference.LAST_NAME
import com.lam.pedro.presentation.screen.profile.ProfilePreference.NATIONALITY
import com.lam.pedro.presentation.screen.profile.ProfilePreference.SEX
import com.lam.pedro.presentation.screen.profile.ProfilePreference.WEIGHT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age

    private val _sex = MutableStateFlow("")
    val sex: StateFlow<String> = _sex

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _nationality = MutableStateFlow("")
    val nationality: StateFlow<String> = _nationality

    init {
        loadProfileData()
    }

    fun updateProfileField(field: ProfilePreference, value: String) {
        when (field) {
            FIRST_NAME -> _firstName.value = value
            LAST_NAME -> _lastName.value = value
            AGE -> _age.value = value
            SEX -> _sex.value = value
            WEIGHT -> _weight.value = value
            HEIGHT -> _height.value = value
            NATIONALITY -> _nationality.value = value
        }
        saveProfileData(field, value)
    }


    // Metodo per caricare i dati del profilo da SecurePreferencesManager
    private fun loadProfileData() {
        _firstName.value = getProfileData(FIRST_NAME, "John")
        _lastName.value = getProfileData(LAST_NAME, "Doe")
        _age.value = getProfileData(AGE, "30")
        _sex.value = getProfileData(SEX, "Male")
        _weight.value = getProfileData(WEIGHT, "75")
        _height.value = getProfileData(HEIGHT, "1.80")
        _nationality.value = getProfileData(NATIONALITY, "USA")
    }

}

