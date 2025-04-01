package com.example.blogapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.blogapp.network.User
import com.google.gson.Gson

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
        prefs.edit().remove("user_data").apply()
    }

    fun saveUser(user: User) {
        val json = gson.toJson(user)
        prefs.edit().putString("user_data", json).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString("user_data", null)
        return if (json != null) gson.fromJson(json, User::class.java) else null
    }
}
