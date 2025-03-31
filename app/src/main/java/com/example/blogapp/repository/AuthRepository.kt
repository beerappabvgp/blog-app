package com.example.blogapp.repository

import android.content.Context
import android.net.Uri
import com.example.blogapp.network.*
import com.example.blogapp.utils.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(context: Context) {
    private val api = RetrofitClient.instance.create(AuthApi::class.java)
    private val tokenManager = TokenManager(context)

    fun login(email: String, password: String, callback: (Response<LoginResponse>?) -> Unit) {
        val request = LoginRequest(email, password)
        api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                callback(response)
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun signup(
        context: Context,
        username: String,
        email: String,
        password: String,
        imageUri: Uri,
        callback: (Response<SignUpResponse>?) -> Unit
    ) {
        val contentResolver = context.contentResolver

        // Read image file as ByteArray
        val inputStream = contentResolver.openInputStream(imageUri)
        val byteArray = inputStream?.readBytes()
        inputStream?.close()

        if (byteArray == null) {
            callback(null)
            return
        }

        // Create multipart request body using the new extension function
        val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("profilePicture", "profile.jpg", requestFile)

        // Convert fields into request bodies using the new approach
        val usernamePart = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())

        // Call API
        val request = api.signup(usernamePart, emailPart, passwordPart, imagePart)
        request.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                callback(response)
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun logout(callback: (Boolean) -> Unit) {
        val token = tokenManager.getToken()

        if (token != null) {
            api.logout("Bearer $token").enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        tokenManager.clearToken() // Clear token after successful logout
                        callback(true)
                    } else {
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(false)
                }
            })
        } else {
            callback(false)
        }
    }


}
