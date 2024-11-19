package com.lam.pedro.presentation.following

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.presentation.screen.loginscreen.User
import com.lam.pedro.presentation.screen.loginscreen.parseUsers
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ViewModelFollowScreen : ViewModel() {


    suspend fun getFollowedUsers(context: Context): Map<User, Boolean> {

        try {
            return parseUsers(
                supabase()
                    .postgrest
                    .rpc("get_users_with_follow_status", buildJsonObject {
                        put("current_user_id", getUUID(context).toString())
                    })
                    .data
            )

        } catch (e: Exception) {
            Log.e(
                "Supabase",
                "Errore durante il caricamento dei dati: ${e.message}  e ${e.cause}",
            )
            return emptyMap()
        }
    }

    /**
     * Metodo che inverte lo stato di follow di un utente, passando da seguito a non seguito e viceversa
     * @param context il contesto dell'applicazione
     * @param followedUser l'utente di cui si vuole invertire lo stato di follow
     */
    suspend fun toggleFollowUser(
        context: Context,
        followedUser: User,
        isAlreadyFollowing: Boolean
    ) {
        try {
            // se l'utente corrente segue già l'altro utente allora rimuovi il follow, altrimenti aggiungilo
            supabase()
                .postgrest
                .rpc(
                    if (isAlreadyFollowing) "remove_follow" else "add_follow",
                    buildJsonObject {
                        put("follower", getUUID(context).toString())
                        put("followed", followedUser.id)
                    })

        } catch (e: Exception) {
            Log.e(
                "Supabase",
                "Errore durante il toggleFollowUser: ${e.message}  e ${e.cause}",
            )
        }
    }

    /**
     * Metodo che carica un file nel bucket di Supabase
     * @param context il contesto dell'applicazione
     * @param fileUri l'uri del file da caricare
     */
    suspend fun uploadFileToSupabase(context: Context, fileUri: Uri) {
        // FIXME: Il file caricato non ha il tipo giusto, probabilmente perchè non viene passato il content type
        try {
            // Leggi i byte del file selezionato
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val fileBytes =
                inputStream?.readBytes() ?: throw Exception("Impossibile leggere il file")
            withContext(Dispatchers.IO) {
                inputStream.close()
            }

            // Carica il file nel bucket di Supabase
            val bucket = supabase().storage.from("avatars")
            val fileName = getUUID(context).toString()

            val result = bucket.upload(fileName, fileBytes) {
                upsert = true
            }

            Log.i("Supabase", "File caricato con successo: $result")
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante il caricamento: ${e.message}", e)
        }
    }


}

@Suppress("UNCHECKED_CAST")
class ViewModelFollowScreenFactory :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelFollowScreen::class.java)) {
            return ViewModelFollowScreen() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}