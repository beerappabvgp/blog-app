package com.example.blogapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.network.LikeBlogResponse
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LikeDislikeBlogViewModel(private val tokenManager: TokenManager) : ViewModel() {
    // Define state flows for like success, loading, error, and like count for each blog
    private val _likeSuccess = MutableStateFlow<Map<String, Boolean>>(emptyMap()) // Maps blogId to liked status
    val likeSuccess: StateFlow<Map<String, Boolean>> = _likeSuccess

    private val _likeCount = MutableStateFlow<Map<String, Int>>(emptyMap()) // Maps blogId to like count
    val likeCount: StateFlow<Map<String, Int>> = _likeCount

    private val _isLoading = MutableStateFlow(false) // true if a request is in progress
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null) // Any error message
    val errorMessage: StateFlow<String?> = _errorMessage

    // Function to like or dislike a blog
    fun toggleLike(blogId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                Log.d("LikeDislikeBlog", "Toggling like for blog with token: $token")
                val response: Response<LikeBlogResponse> = BlogRetrofitClient.instance.likeBlog(token, blogId)
                Log.d("LikeDislikeBlog", "Response: $response")

                if (response.isSuccessful) {
                    // Update the state for the specific blog
                    response.body()?.let {
                        // Update the likeSuccess and likeCount for this blogId
                        _likeSuccess.value = _likeSuccess.value + (blogId to it.liked)
                        _likeCount.value = _likeCount.value + (blogId to it.likeCount)
                        Log.d("LikeDislikeBlog", "Like success: ${_likeSuccess.value[blogId]}, Like count: ${_likeCount.value[blogId]}")
                    }
                } else {
                    _errorMessage.value = "Failed to like/dislike the blog"
                    Log.d("LikeDislikeBlog", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("LikeDislikeBlog", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
