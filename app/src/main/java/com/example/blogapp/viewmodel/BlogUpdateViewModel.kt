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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class EditBlogViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var updatedBlog: Blog? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)

    fun updateBlog(
        blogId: String,
        title: String,
        content: String,
        images: List<MultipartBody.Part>,
        imagesToDelete: List<String>
    ) {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"

            try {
                val titlePart = RequestBody.create(MultipartBody.FORM, title)
                val contentPart = RequestBody.create(MultipartBody.FORM, content)

                // Convert imagesToDelete list into a JSON string (if required)
                val imagesToDeleteJson = RequestBody.create(
                    MultipartBody.FORM,
                    imagesToDelete.joinToString(",") { "\"$it\"" }
                )

                val response: Response<Blog> = BlogRetrofitClient.instance.updateBlog(
                    blogId,
                    token,
                    titlePart,
                    contentPart,
                    images, // New images as MultipartBody.Part
                    imagesToDeleteJson // Images to delete as JSON
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

