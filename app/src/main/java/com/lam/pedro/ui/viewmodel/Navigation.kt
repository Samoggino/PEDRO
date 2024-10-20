package com.lam.pedro.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lam.pedro.ui.screens.HomeScreen
import com.lam.pedro.ui.screens.LoginScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen() }
    }
}