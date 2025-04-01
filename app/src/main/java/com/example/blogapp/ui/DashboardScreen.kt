package com.example.blogapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.network.Blog
import com.example.blogapp.network.User
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.DashboardViewModel
import com.example.blogapp.viewmodel.DashboardViewModelFactory
import com.example.blogapp.viewmodel.ThemeViewModel
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    // Create and initialize the DashboardViewModel
    val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(tokenManager))

    // Fetch user-specific blogs
    LaunchedEffect(Unit) {
        dashboardViewModel.fetchAllBlogs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val user = tokenManager.getUser()
        user?.let {
            // Display user info (profile picture, username, etc.)
            Image(
                painter = rememberAsyncImagePainter(it.profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Welcome, ${it.username}!", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Email: ${it.email}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "About: ${it.about}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // Show user's blogs
            if (dashboardViewModel.isLoading) {
                CircularProgressIndicator()
            } else if (dashboardViewModel.errorMessage != null) {
                Text(text = dashboardViewModel.errorMessage ?: "Unknown Error", color = Color.Red)
            } else {
                // Display user's blogs if available
                LazyColumn {
                    items(dashboardViewModel.blogs) { blog ->
                        BlogItem(navController, blog = blog)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                tokenManager.clearToken()
                navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
            }) {
                Text("Logout")
            }
        } ?: run {
            Text("No user data available", fontSize = 16.sp)
        }
    }
}

@Composable
fun BlogItem(
    navController: NavController,
    blog: Blog,
) {
    val viewModel: ThemeViewModel = viewModel()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val cardColor = if (isDarkTheme) {
        Color.Black // Dark background for dark theme
    } else {
        Color.White // Light background for light theme
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { navController.navigate("blogDetail/${blog._id}") }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Blog Image (if available)
            blog.images?.firstOrNull()?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Blog Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Blog Title
            Text(
                text = blog.title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Blog Content (Preview)
            Text(
                text = blog.content,
                fontSize = 16.sp,
                color = Color(0xFF666666),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edit & Delete Buttons with Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Edit Button
                Button(
                    onClick = { navController.navigate("editBlog/${blog._id}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)), // Blue
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit", fontSize = 16.sp, color = Color.White)
                }


                Spacer(modifier = Modifier.width(12.dp))

                // Delete Button
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63946)), // Red
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
