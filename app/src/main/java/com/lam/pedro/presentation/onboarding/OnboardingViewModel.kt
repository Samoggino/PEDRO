package com.lam.pedro.presentation.onboarding

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveProfileData
import com.lam.pedro.presentation.screen.profile.ProfilePreference

const val MAX_NAME_LENGTH = 15
const val MIN_AGE = 1
const val MAX_AGE = 120
const val MIN_WEIGHT = 20.0
const val MAX_WEIGHT = 300.0
const val MIN_HEIGHT = 50.0
const val MAX_HEIGHT = 250.0
const val MALE = "male"
const val FEMALE = "female"


class OnboardingViewModel : ViewModel() {

    // Mutable states for the third page fields
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val age = mutableStateOf("")
    val sex = mutableStateOf("")
    val weight = mutableStateOf("")
    val height = mutableStateOf("")
    val nationality = mutableStateOf("")

    // Validation state
    private val isThirdPageValid = derivedStateOf {
        firstName.value.isNotEmpty() && firstName.value.length <= MAX_NAME_LENGTH &&
                lastName.value.isNotEmpty() && lastName.value.length <= MAX_NAME_LENGTH &&
                age.value.toIntOrNull()?.let { it in MIN_AGE..MAX_AGE } == true &&
                (sex.value == MALE || sex.value == FEMALE) &&
                // peso tra i 20 e i 300 kg
                weight.value.toDoubleOrNull() != null && weight.value.toDoubleOrNull()!! in MIN_WEIGHT..MAX_WEIGHT &&
                // altezza tra i 50 e i 250 cm
                height.value.toDoubleOrNull() != null && height.value.toDoubleOrNull()!! in MIN_HEIGHT..MAX_HEIGHT &&
                nationality.value.isNotEmpty()
    }

    fun areProfileFieldsValid(): Boolean {

        return if (isThirdPageValid.value) {
            saveProfileData(ProfilePreference.AGE, age.value)
            saveProfileData(ProfilePreference.FIRST_NAME, firstName.value)
            saveProfileData(ProfilePreference.LAST_NAME, lastName.value)
            saveProfileData(ProfilePreference.SEX, sex.value)
            saveProfileData(ProfilePreference.WEIGHT, weight.value)
            saveProfileData(ProfilePreference.HEIGHT, height.value)
            saveProfileData(ProfilePreference.NATIONALITY, nationality.value)

            true
        } else {
            false
        }
    }
}

class OnboardingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}