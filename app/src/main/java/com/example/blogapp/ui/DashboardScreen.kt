package com.example.blogapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import com.example.blogapp.network.User
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.AuthViewModel
import com.example.blogapp.viewmodel.AuthViewModelFactory
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(tokenManager))

    // Collect the user state from the ViewModel
    val user = authViewModel.userProfile.collectAsState().value
    Log.d("DashBoardScreen", "user data: $user")

    // If user profile is not null, navigate to the dashboard
    if (user != null) {
        LaunchedEffect(user) {
            Log.d("DashBoardScreen", "Navigating to dashboard")
            navController.navigate("dashboard")
        }
    } else {
        Log.d("DashBoardScreen", "User profile is null")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text("Welcome, ${user.username}", fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text("Email: ${user.email}", fontSize = 18.sp)
            Text("Username: ${user.username}", fontSize = 18.sp)
        } else {
            Text("No user profile found", fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("login") // Navigate to login page after logout
        }) {
            Text("Logout")
        }
    }
}

