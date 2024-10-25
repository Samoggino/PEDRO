package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

@Composable
fun PermissionRequired(color: Long, onPermissionLaunch: () -> Unit) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = stringResource(R.string.permissions_required),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(32.dp))
    Image(
        modifier = Modifier
            .size(90.dp),
        painter = painterResource(id = R.drawable.permission_icon),
        contentDescription = stringResource(id = R.string.app_logo),
        colorFilter = ColorFilter.tint(Color(color)) // Applica il colore
    )
    Spacer(modifier = Modifier.height(32.dp))
    Button(
        onClick = { onPermissionLaunch() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(color), // Colore di sfondo
            contentColor = Color.White         // Colore del testo
        )
    ) {
        Text(text = stringResource(R.string.permissions_button_label))
    }
}