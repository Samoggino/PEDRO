package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.presentation.screen.more.loginscreen.parseUsers
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class CommunityScreenViewModel : ViewModel() {

    private val _userIsLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val userIsLoggedIn: StateFlow<Boolean> = _userIsLoggedIn

    init {
        _userIsLoggedIn.value = getUUID() != null
    }

    private val _userFollowMap = MutableStateFlow<Map<User, Boolean>>(emptyMap())
    val userFollowMap: StateFlow<Map<User, Boolean>> = _userFollowMap

    private val _isInitialLoad: MutableStateFlow<Boolean> = MutableStateFlow(true) // Nuovo flag
    val isInitialLoad: StateFlow<Boolean> = _isInitialLoad


    /**
     * Metodo per recuperare gli utenti seguiti.
     */
    suspend fun getFollowedUsers() {
        try {
            val result = parseUsers(
                supabase().postgrest
                    .rpc("get_users_with_follow_status", buildJsonObject {
                        put("current_user_id", getUUID().toString())
                    }).data
            )
            _userFollowMap.value = result
            _isInitialLoad.value = false // Impostiamo il flag a false dopo il caricamento
        } catch (e: Exception) {
            _userFollowMap.value = emptyMap()
            _isInitialLoad.value = false // Anche in caso di errore
            Log.e("Supabase", "Errore durante il recupero degli utenti seguiti: ${e.message}", e)
        }
    }

    // Metodo per aggiornare lo stato di follow
    fun updateFollowState(updatedMap: Map<User, Boolean>) {
        _userFollowMap.value = updatedMap
    }

    /**
     * Metodo per invertire lo stato di follow di un utente.
     * @param followedUser l'utente da seguire/non seguire
     * @param isAlreadyFollowing lo stato attuale di follow
     */
    suspend fun toggleFollowUser(
        followedUser: User,
        isAlreadyFollowing: Boolean
    ) {
        try {
            val rpcFunction = if (isAlreadyFollowing) "remove_follow" else "add_follow"
            val response = supabase().postgrest.rpc(rpcFunction, buildJsonObject {
                put("follower", getUUID().toString())
                put("followed", followedUser.id)
            })
            Log.i("Supabase", "Follow state updated successfully: $response")
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante il toggleFollowUser: ${e.message}", e)
        }
    }

    fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (userIsLoggedIn.value) {
                getFollowedUsers()
            } else {
                _isInitialLoad.value = false // Impostiamo a false se l'utente non è loggato
            }
        }
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