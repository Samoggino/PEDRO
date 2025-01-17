package com.lam.pedro.presentation.onboarding

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveProfileData
import com.lam.pedro.presentation.screen.profile.ProfilePreference

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
        firstName.value.isNotEmpty() && firstName.value.length <= 15 &&
                lastName.value.isNotEmpty() && lastName.value.length <= 15 &&
                age.value.toIntOrNull()?.let { it in 1..120 } == true &&
                (sex.value == "male" || sex.value == "female") &&
                weight.value.toDoubleOrNull() != null &&
                height.value.toDoubleOrNull() != null &&
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