package com.lam.pedro.presentation.screen

import com.lam.pedro.presentation.theme.PedroYellow
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

@Composable
fun AboutScreen(

) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier
                .weight(1f)
                .size(90.dp),
            painter = painterResource(id = R.drawable.mexican_hat_svgrepo_com),
            contentDescription = stringResource(id = R.string.app_logo),
            colorFilter = ColorFilter.tint(PedroYellow) // Applica il colore
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.about_app_name), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.about_app_name_acronym), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.app_version), style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.about_app_developers), style = MaterialTheme.typography.titleSmall)






    }

}