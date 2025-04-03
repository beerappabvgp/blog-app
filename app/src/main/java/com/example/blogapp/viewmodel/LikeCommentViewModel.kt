package com.example.blogapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.BlogRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LikeCommentViewModel(initialLikeCount: Int, userLiked: Boolean) : ViewModel() {

    // StateFlow for tracking like count
    private val _likeCount = MutableStateFlow(initialLikeCount)
    val likeCount: StateFlow<Int> = _likeCount

    // StateFlow for tracking if user liked the comment
    private val _isLiked = MutableStateFlow(userLiked)
    val isLiked: StateFlow<Boolean> = _isLiked

    // StateFlow for handling errors
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Function to like/unlike a comment
    fun toggleLike(blogId: String, commentId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = BlogRetrofitClient.instance.likeComment(token, blogId, commentId)
                if (response.isSuccessful && response.body() != null) {
                    val wasLiked = _isLiked.value
                    _isLiked.value = !wasLiked // Toggle like status
                    _likeCount.value += if (wasLiked) -1 else 1 // Update like count
                } else {
                    _errorMessage.value = "Failed to update like status"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "Network error: ${e.message}"
            } catch (e: IOException) {
                _errorMessage.value = "Check your internet connection"
            }
        }
    }
}
