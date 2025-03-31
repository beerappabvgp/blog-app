package com.example.blogapp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogapp.network.LoginResponse
import com.example.blogapp.network.User
import com.example.blogapp.repository.AuthRepository
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

// ViewModel to manage authentication state
class AuthViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> get() = _loginResponse

    // Change to StateFlow to make it observable by Compose
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    class UserProfile {

    }


    fun handleLoginResponse(response: Response<LoginResponse>?, onResult: (Boolean, String?) -> Unit) {
        if (response?.isSuccessful == true) {
            val authResponse = response.body()
            Log.d("AuthViewModel", "Response body: $authResponse")
            if (authResponse?.token != null) {
                _loginResponse.value = authResponse
                Log.i("AuthViewModel", "loginResponse: ${_loginResponse.value}")
                _userProfile.value = authResponse.user // ✅ Update user profile state
                Log.i("AuthViewModel", "User profile updated: ${_userProfile.value}")
                tokenManager.saveToken(authResponse.token)
                Log.i("AuthViewModel", "Token saved: ${authResponse.token}")
                onResult(true, null)
            } else {
                onResult(false, "Invalid credentials")
            }
        } else {
            val errorMsg = response?.errorBody()?.string() ?: "Unknown error"
            onResult(false, errorMsg)
        }
    }

}
