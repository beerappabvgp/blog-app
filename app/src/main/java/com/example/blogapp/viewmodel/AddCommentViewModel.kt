package com.example.blogapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.network.Comment
import com.example.blogapp.network.CreateCommentResponse
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddCommentViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _newComments = MutableStateFlow<Map<String, Comment>>(emptyMap())
    val newComments: StateFlow<Map<String, Comment>> get() = _newComments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    /**
     * Adds a new comment to a specific blog post.
     * @param blogId The ID of the blog to which the comment is being added.
     * @param content The comment text.
     * @param images Optional list of image files attached to the comment.
     */
    fun addComment(blogId: String, content: String, images: List<MultipartBody.Part>?) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = "Bearer ${tokenManager.getToken()}"

            try {
                val contentPart = RequestBody.create("text/plain".toMediaTypeOrNull(), content)

                val response: Response<CreateCommentResponse> = BlogRetrofitClient.instance.addComment(
                    token, blogId, contentPart, images
                )

                if (response.isSuccessful) {
                    val latestComment = response.body()?.blog?.comments?.lastOrNull()
                    if (latestComment != null) {
                        _newComments.value = _newComments.value + (blogId to latestComment)
                    }
                } else {
                    _errorMessage.value = "Failed to add comment: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                Log.d("AddCommentViewModel", "Error: ${e.message}")
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
