package com.lam.pedro.presentation.component

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.SecurePreferencesManager.getMyContext
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SecurePreferencesManager.updateAvatarUrl
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.more.loginscreen.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.launch

@Composable
fun UploadAvatarButton(viewModel: UploadAvatarViewModel = viewModel(factory = UploadAvatarViewModelFactory())) {
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadFileToSupabase(it) }
    }

    Button(
        onClick = { pickFileLauncher.launch("image/*") },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Carica avatar")
    }
}

class UploadAvatarViewModel : ViewModel() {

    fun uploadFileToSupabase(fileUri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = getMyContext().contentResolver.openInputStream(fileUri)
                    ?: throw Exception("Impossibile leggere il file, URI non valido.")

                val fileBytes = inputStream.readBytes()
                inputStream.close()

                val bucket = supabase().storage.from("avatars")
                val fileName = getUUID().toString()

                // Specifica correttamente il content type
                val result = bucket.upload(fileName, fileBytes) {
                    upsert = true
                    contentType = ContentType.Image.PNG
                    contentType = ContentType.Image.JPEG
                }

                Log.d("Supabase", "Risultato caricamento: $result")

                val newAvatarUrl =
                    "https://tfgeogkbrvekrzsgpllc.supabase.co/storage/v1/object/public/avatars/$fileName"

                updateAvatarUrlInDatabase(newAvatarUrl)

            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il caricamento: ${e.message}", e)
            }
        }
    }

    private suspend fun updateAvatarUrlInDatabase(newAvatarUrl: String) {
        val user = supabase().from("users").update({ set("avatar", newAvatarUrl) }) {
            select()
            filter { eq("id", getUUID()!!) }
        }.decodeSingle<User>()

        updateAvatarUrl(user.avatarUrl)
        Log.d("Supabase", "Avatar aggiornato anche in sharedPreferences")
    }
}


class UploadAvatarViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadAvatarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadAvatarViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
