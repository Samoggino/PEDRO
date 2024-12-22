package com.lam.pedro.presentation.screen.community

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.loginscreen.User
import com.lam.pedro.presentation.screen.loginscreen.parseUsers
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ViewModelFollowScreen : ViewModel() {

    private val _userFollowMap = MutableStateFlow<Map<User, Boolean>?>(null)
    val userFollowMap: StateFlow<Map<User, Boolean>?> = _userFollowMap

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
        } catch (e: Exception) {
            _userFollowMap.value = emptyMap() // Stato di errore
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

    /**
     * Metodo che carica un file nel bucket di Supabase.
     * @param fileUri l'uri del file da caricare
     */
    fun uploadFileToSupabase(fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream =
                    SecurePreferencesManager.appContext!!.contentResolver.openInputStream(fileUri)
                        ?: throw Exception("Impossibile leggere il file, URI non valido.")

                val fileBytes = inputStream.readBytes()
                withContext(Dispatchers.IO) {
                    inputStream.close()
                }

                val bucket = supabase().storage.from("avatars")
                val fileName = getUUID().toString()

                // Specifica correttamente il content type
                val result = bucket.upload(fileName, fileBytes) {
                    upsert = true
                    contentType = ContentType.Image.PNG
                    contentType = ContentType.Image.JPEG
                }

                Log.i("Supabase", "File caricato con successo: $result")
            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il caricamento: ${e.message}", e)
            }
        }
    }
}

// Factory per la creazione del ViewModel
@Suppress("UNCHECKED_CAST")
class ViewModelFollowScreenFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelFollowScreen::class.java)) {
            return ViewModelFollowScreen() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
