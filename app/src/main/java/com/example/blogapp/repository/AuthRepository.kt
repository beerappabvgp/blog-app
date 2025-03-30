package com.example.blogapp.repository

import com.example.blogapp.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val api = RetrofitClient.instance.create(AuthApi::class.java)

    fun login(email: String, password: String, callback: (Response<AuthResponse>?) -> Unit) {
        val request = LoginRequest(email, password)
        api.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                callback(response)
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun signup(email: String, password: String, callback: (Response<AuthResponse>?) -> Unit) {
        val request = SignupRequest(email, password)
        api.signup(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                callback(response)
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}
