package com.example.blogapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.Blog
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.network.BlogUpdateRequest
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class EditBlogViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var updatedBlog: Blog? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    // This function will be used to update the blog
    fun updateBlog(blogId: String, title: String, content: String, images: List<String>, imagesToDelete: List<String>) {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                // Create a BlogUpdateRequest containing the updated title, content, and images
                val request = BlogUpdateRequest(title, content, images, imagesToDelete)

                val response: Response<Blog> = BlogRetrofitClient.instance.updateBlog(
                    blogId,
                    request,  // Send the request object which contains both the blog and imagesToDelete
                    token
                )

                if (response.isSuccessful) {
                    updatedBlog = response.body()
                } else {
                    errorMessage = "Failed to update blog"
                    updatedBlog = null
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                updatedBlog = null
            } finally {
                isLoading = false
            }
        }
    }


}
