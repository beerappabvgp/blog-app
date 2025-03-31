package com.example.blogapp.ui

import LoginButton
import LogoutButton
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.blogapp.ui.components.SignUpButton
import com.example.blogapp.ui.components.ThemeToggleButton
import com.example.blogapp.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, themeViewModel: ThemeViewModel) {
    // ✅ Get the current screen route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blog App") },
                actions = {
                    ThemeToggleButton(themeViewModel) // 🔥 Global Theme Toggle Button

                    // ✅ Show different buttons based on the current route
                    when (currentRoute) {
                        "landing" -> { // 🔥 Show BOTH Sign Up & Login on Landing Page
                            SignUpButton(navController)
                            LoginButton(navController)
                        }
                        "login" -> SignUpButton(navController)
                        "signup" -> LoginButton(navController)
                        else -> LogoutButton(navController)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AppNavigation(navController)
        }
    }
}
