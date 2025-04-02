package com.example.blogapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.network.LikeBlogResponse
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class LikeDislikeBlogViewModel(private val tokenManager: TokenManager) : ViewModel() {

    fun toggleLike(blogId: String, onResult: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.getToken()}"
            try {
                val response: Response<LikeBlogResponse> = BlogRetrofitClient.instance.likeBlog(token, blogId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult(it.liked, it.likeCount) // Pass updated data to UI
                    }
                } else {
                    Log.d("LikeDislikeBlog", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("LikeDislikeBlog", "Error: ${e.message}")
            }
        }
    }
}
