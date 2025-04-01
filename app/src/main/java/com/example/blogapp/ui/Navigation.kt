package com.example.blogapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.blogapp.ui.components.BlogDetailScreen
import com.example.blogapp.ui.screens.CreateBlogScreen
import com.example.blogapp.ui.screens.EditBlogScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("createBlog") { CreateBlogScreen(navController) }
        composable("blogDetail/{blogId}") { backStackEntry ->
            val blogId = backStackEntry.arguments?.getString("blogId") ?: return@composable
            BlogDetailScreen(navController, blogId)
        }
        composable("editBlog/{blogId}") { backStackEntry ->
            val blogId = backStackEntry.arguments?.getString("blogId") ?: ""
            EditBlogScreen(navController, blogId)
        }
    }
}
