package com.example.blogapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.network.DeleteBlogResponse
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class DeleteBlogViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var deleteSuccess by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    fun deleteBlog(blogId: String) {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                Log.d("DeleteBlog: ", "deleted the token ... $token")
                val response: Response<DeleteBlogResponse> = BlogRetrofitClient.instance.deleteBlog(blogId, token)
                if (response.isSuccessful) {
                    deleteSuccess = true
                } else {
                    errorMessage = "Failed to delete blog"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}