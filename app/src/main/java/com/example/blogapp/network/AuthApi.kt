package com.example.blogapp.network

import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part


// Data class for login response
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

// Data class for signup response
data class SignUpResponse(
    val message: String,
    val user: User
)

// Nested data class for user details in the response
data class User(
    val username: String,
    val email: String,
    val about: String,
    val profilePicture: String
)
// Data class for user login request
data class LoginRequest(val email: String, val password: String)

// Data class for signup request
data class SignupRequest(val username: String, val email: String, val password: String, val imageUri: Uri)

// Data class for API responses
data class AuthResponse(val token: String, val message: String)

// API interface
interface AuthApi {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("auth/signup")
    fun signup(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("about") about: RequestBody,
        @Part("password") password: RequestBody,
        @Part profilePicture: MultipartBody.Part
    ): Call<SignUpResponse>

    @POST("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>
}
