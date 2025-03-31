package com.example.blogapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SignUpButton(navController: NavHostController) {
    TextButton(onClick = { navController.navigate("signup") }) {
        Text("Sign Up")
    }
}
