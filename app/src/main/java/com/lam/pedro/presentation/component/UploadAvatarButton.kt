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
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UploadAvatarButton(viewModel: UploadAvatarViewModel = viewModel(factory = UploadAvatarViewModelFactory())) {

    // Launcher per aprire il file picker
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedFileUri ->
            Log.i("Community", "FileUploadButton")
            viewModel.uploadFileToSupabase(selectedFileUri)
        }
    }

    // Bottone per attivare il file picker
    Button(
        onClick = { pickFileLauncher.launch("image/*") },
        modifier = Modifier.padding(16.dp)
    ) { Text("Carica avatar") }
}

class UploadAvatarViewModel : ViewModel() {

    /**
     * Metodo che carica un file nel bucket di Supabase.
     * @param fileUri l'uri del file da caricare
     */
    fun uploadFileToSupabase(fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream =
                    SecurePreferencesManager.getMyContext().contentResolver.openInputStream(fileUri)
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

class UploadAvatarViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadAvatarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadAvatarViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
