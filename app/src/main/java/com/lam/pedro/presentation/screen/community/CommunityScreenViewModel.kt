package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.communityRepository.CommunityRepository
import com.lam.pedro.data.datasource.communityRepository.CommunityRepositoryImpl
import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CommunityScreenViewModel : ViewModel() {

    private val communityRepository: CommunityRepository =
        CommunityRepositoryImpl() // Usa il repository

    private val _userFollowMap = MutableStateFlow<Map<User, Boolean>>(emptyMap())
    val userFollowMap: StateFlow<Map<User, Boolean>> = _userFollowMap

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isInitialLoad = MutableStateFlow(true)
    val isInitialLoad: StateFlow<Boolean> = _isInitialLoad

    private val _userIsLoggedIn = MutableStateFlow(false)
    val userIsLoggedIn: StateFlow<Boolean> = _userIsLoggedIn

    /**
     * Se l'utente è loggato, carica i dati, altrimenti non fa nulla
     */
    init {
        _userIsLoggedIn.value = getUUID() != null
        if (_userIsLoggedIn.value) {
            loadFollowedUsers()
        } else {
            _isInitialLoad.value = false
        }
    }

    /**
     * Funzione per aggiornare i dati
     */
    fun refreshData() {
        setRefreshing(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (userIsLoggedIn.value) {
                try {
                    loadFollowedUsers()
                } catch (e: Exception) {
                    Log.e("Community", "Errore durante il refresh: ${e.message}")
                } finally {
                    setRefreshing(false)
                }
            }
        }
    }


    /**
     * Funzione per caricare gli utenti seguiti
     */
    private fun loadFollowedUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                // Esegui il recupero dei dati
                val result = communityRepository.getFollowedUsers()
                _userFollowMap.value = result
            } catch (e: Exception) {
                // Gestisci l'errore
                _userFollowMap.value = emptyMap()
                Log.e("Community", "Errore durante il recupero degli utenti seguiti: ${e.message}")
            }
            _isRefreshing.value = false
            _isInitialLoad.value = false
        }
    }

    /**
     * Funzione per gestire il toggle del follow
     */
    fun toggleFollowUser(followedUser: User, isAlreadyFollowing: Boolean) {
        viewModelScope.launch {
            try {
                communityRepository.toggleFollowUser(followedUser, isAlreadyFollowing)
                updateFollowState(followedUser, !isAlreadyFollowing)
            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il toggleFollowUser: ${e.message}", e)
            }
        }
    }

    /**
     * Funzione per aggiornare lo stato di follow
     */
    private fun updateFollowState(user: User, isFollowing: Boolean) {
        _userFollowMap.value = _userFollowMap.value.toMutableMap().apply {
            this[user] = isFollowing
        }
    }

    /**
     * Funzione per impostare lo stato di refreshing
     */
    private fun setRefreshing(value: Boolean) {
        _isRefreshing.value = value
    }
}

class CommunityScreenViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}