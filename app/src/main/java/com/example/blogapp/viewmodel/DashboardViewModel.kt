package com.example.blogapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.Blog
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var blogs: List<Blog> by mutableStateOf(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    fun fetchAllBlogs() {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                val response: Response<List<Blog>> = BlogRetrofitClient.instance.getUserPosts(token)
                if (response.isSuccessful) {
                    blogs = response.body() ?: emptyList()
                } else {
                    errorMessage = "Failed to fetch blogs"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
