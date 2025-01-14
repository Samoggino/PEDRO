package com.lam.pedro.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

@Composable
fun HomeScreen(onProfileClick: () -> Unit) {

    Log.i("Reload", "HomeScreen loaded out")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
            Row {
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        onProfileClick()
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .height(180.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                // TODO: graph
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Something else",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .height(180.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                // TODO: graph
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Booooooooh",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .height(180.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                // TODO: graph
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
