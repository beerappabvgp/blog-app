package com.example.blogapp.ui

import LoginButton
import LogoutButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.blogapp.ui.components.SignUpButton
import com.example.blogapp.ui.components.ThemeToggleButton
import com.example.blogapp.ui.screens.BlogListScreen
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, themeViewModel: ThemeViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val token = tokenManager.getToken()
    val isLoggedIn = !token.isNullOrEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Blog App",
                        modifier = Modifier.clickable {
                            navController.navigate("landing") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            }
                        }
                    )
                },
                actions = {
                    ThemeToggleButton(themeViewModel)

                    when (currentRoute) {
                        "landing" -> {
                            if (isLoggedIn) {
                                // Dashboard button shown when logged in
                                TextButton(onClick = { navController.navigate("dashboard") }) {
                                    Text("Dashboard")
                                }
                                IconButton(onClick = { navController.navigate("createBlog") }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Create Blog")
                                }
                                LogoutButton(navController)
                            } else {
                                SignUpButton(navController)
                                LoginButton(navController)
                            }
                        }
                        "login" -> SignUpButton(navController)
                        "signup" -> LoginButton(navController)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isLoggedIn && currentRoute == "landing") {
                BlogListScreen(navController) // ✅ Show blog list when logged in
            } else {
                AppNavigation(navController)
            }
        }
    }
}
