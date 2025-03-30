package com.example.blogapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.blogapp.network.AuthResponse
import com.example.blogapp.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val authRepository = AuthRepository()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Button(onClick = {
            authRepository.login(email, password) { response ->
                if (response?.isSuccessful == true) {
                    val authResponse = response.body()
                    if (authResponse?.token != null) {
                        message = authResponse.message ?: "Login successful"
                        token = authResponse.token
                        navController.navigate("dashboard")
                    } else {
                        message = "Login failed: Invalid credentials"
                    }
                } else {
                    val errorMsg = response?.errorBody()?.string() ?: "Unknown error"
                    message = "Login failed: $errorMsg"
                }
            }
        }) {
            Text("Login")
        }
        Text(message)
    }
}

