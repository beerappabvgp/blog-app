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

class BlogDetailViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var blog: Blog? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    fun fetchBlogDetails(blogId: String) {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                val response: Response<Blog> = BlogRetrofitClient.instance.getBlogDetails(token, blogId)
                if (response.isSuccessful) {
                    blog = response.body()
                } else {
                    errorMessage = "Failed to load blog details"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
