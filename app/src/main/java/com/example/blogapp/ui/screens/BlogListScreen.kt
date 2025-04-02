package com.example.blogapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.blogapp.network.Blog
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.ui.components.BlogItem
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.LikeDislikeBlogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BlogListScreen(navController: NavController) {
    val context = LocalContext.current
    val token = "Bearer ${TokenManager(context).getToken()}"
    val blogs = remember { mutableStateListOf<Blog>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val tokenManager = TokenManager(context)
    val likeDislikeBlogViewModel: LikeDislikeBlogViewModel = viewModel { LikeDislikeBlogViewModel(tokenManager) }
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                BlogRetrofitClient.instance.getBlogs(token)
            }
            if (response.isSuccessful) {
                blogs.clear()
                response.body()?.let { blogs.addAll(it) }
            } else {
                errorMessage = "Error: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load blogs: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Latest Blogs", style = MaterialTheme.typography.headlineMedium)

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            else -> LazyColumn {
                items(blogs) { blog ->
                    BlogItem(blog, navController, tokenManager)
                }
            }
        }
    }
}
