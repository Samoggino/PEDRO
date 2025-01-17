package com.lam.pedro.presentation.screen.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkUserLoggedIn
import com.lam.pedro.presentation.screen.more.loginscreen.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    onNavBack: () -> Unit,
    onNavigate: (String) -> Unit,
    titleId: Int,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackButton { onNavBack() } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {

        }
    }

}