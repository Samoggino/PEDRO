package com.lam.pedro.presentation.screen.more.importExport

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.R
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl
import com.lam.pedro.presentation.screen.MenuItem
import com.lam.pedro.presentation.serialization.ResultState


@Composable
fun ImportButton(
    viewModel: ImportExportViewModel = viewModel(
        factory = ImportExportViewModelFactory(
            ActivitySupabaseSupabaseRepositoryImpl()
        )
    ),
) {
    val importResult by viewModel.importResult.observeAsState(ResultState.Idle) // Osserva lo stato dell'import
    val message by viewModel.messageEvent.observeAsState() // Messaggi da mostrare tramite Toast

    // Mostra il messaggio, se presente
    message?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
        viewModel.dataMutable.value = null
    }

    // Launcher per aprire il file picker
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedFileUri ->
            viewModel.importJsonToDatabase(selectedFileUri)
            Log.d("JSON", "Selected file: $selectedFileUri")
        }
    }

    // Mostra il MenuItem
    MenuItem(
        iconId = R.drawable.upload_db_icon, // Sostituisci con l'icona che preferisci
        label = "Import Data",
        onClick = {
            if (importResult != ResultState.Loading) {
                pickFileLauncher.launch(input = "*/*")
            }
        },
        enabled = importResult != ResultState.Loading,
        height = 70,
        extraIcon = {
            if (importResult == ResultState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        },
        finalIcon = Icons.Filled.TouchApp
    )
}

@Composable
fun ExportButton(
    viewModel: ImportExportViewModel = viewModel(
        factory = ImportExportViewModelFactory(
            ActivitySupabaseSupabaseRepositoryImpl()
        )
    ),
) {
    val saveResult by viewModel.saveResult.observeAsState(ResultState.Idle) // Stato del salvataggio
    val message by viewModel.messageEvent.observeAsState() // Messaggi da mostrare tramite Toast

    // Mostra il messaggio, se presente
    message?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
        viewModel.dataMutable.value = null
    }

    // Mostra il MenuItem
    MenuItem(
        iconId = R.drawable.export_db_icon, // Sostituisci con l'icona che preferisci
        label = "Export Data",
        onClick = {
            if (saveResult != ResultState.Loading) {
                viewModel.exportFromDB()
            }
        },
        height = 70,
        extraIcon = {
            if (saveResult == ResultState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        },
        finalIcon = Icons.Filled.TouchApp
    )
}
