package com.example.blogapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.blogapp.network.BlogApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.network.Blog
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.BlogDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(navController: NavController, blogId: String) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val viewModel: BlogDetailViewModel = viewModel { BlogDetailViewModel(tokenManager) }

    LaunchedEffect(blogId) {
        viewModel.fetchBlogDetails(blogId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Blog Details") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator()
                }
                !viewModel.errorMessage.isNullOrEmpty() -> {
                    Text(viewModel.errorMessage ?: "Unknown error", color = Color.Red)
                }
                viewModel.blog != null -> {
                    val blog = viewModel.blog!!
                    BlogItem(blog = blog)
                }
            }
        }
    }
}

