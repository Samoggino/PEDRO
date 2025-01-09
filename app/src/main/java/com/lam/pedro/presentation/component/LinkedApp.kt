package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.presentation.theme.PedroYellow

@Composable
fun LinkedApp(appLogo: Int) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .size(90.dp),
            painter = painterResource(id = appLogo),
            contentDescription = stringResource(id = R.string.health_connect_logo)
        )
        Image(
            modifier = Modifier
                .weight(1f)
                .size(50.dp),
            painter = painterResource(id = R.drawable.link),
            contentDescription = stringResource(id = R.string.link_logo)
        )
        Image(
            modifier = Modifier
                .weight(1f)
                .size(90.dp),
            painter = painterResource(id = R.drawable.pedro_icon),
            contentDescription = stringResource(id = R.string.app_logo),
            colorFilter = ColorFilter.tint(PedroYellow) // Applica il colore
        )


    }

}