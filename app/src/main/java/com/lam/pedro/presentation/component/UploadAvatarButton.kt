package com.lam.pedro.presentation.component

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.presentation.screen.community.CommunityScreenViewModel
import com.lam.pedro.presentation.screen.community.CommunityScreenViewModelFactory

@Composable
fun UploadAvatarButton(viewModel: CommunityScreenViewModel = viewModel(factory = CommunityScreenViewModelFactory())) {

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
