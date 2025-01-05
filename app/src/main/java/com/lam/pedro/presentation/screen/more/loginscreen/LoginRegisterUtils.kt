package com.lam.pedro.presentation.screen.more.loginscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.presentation.component.LinkedApp

@Composable
fun PersonalInfoField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Email", // Default label is "Email"
    icon: ImageVector = Icons.Filled.Email,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp)),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = { Icon(icon, contentDescription = null) },
        modifier = modifier
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password", // Default label is "Password"
    isPasswordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isPasswordVisible) }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Nascondi password" else "Mostra password"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier
    )
}

@Composable
fun LoginRegisterDescriptor(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(40.dp))

    LinkedApp(R.drawable.supabase_logo_icon)

    Spacer(modifier = Modifier.height(40.dp))
}