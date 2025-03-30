package com.example.blogapp.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

// Data class for user login request
data class LoginRequest(val email: String, val password: String)

// Data class for signup request
data class SignupRequest(val email: String, val password: String)

// Data class for API responses
data class AuthResponse(val token: String, val message: String)

// API interface
interface AuthApi {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/signup")
    fun signup(@Body request: SignupRequest): Call<AuthResponse>
}
