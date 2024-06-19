package com.bangkit.talkee.data.preference

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, idToken: String, expireTime: Long) {
        with(sharedPreferences.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("ID_TOKEN", idToken)
            putLong("ID_TOKEN_EXPIRE", expireTime)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    fun getIDToken(): String? {
        return sharedPreferences.getString("ID_TOKEN", null)
    }

    fun getTokenExpire(): Long {
        return sharedPreferences.getLong("ID_TOKEN_EXPIRE", 0L)
    }

    fun clearTokens() {
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.remove("ID_TOKEN")
        editor.remove("ID_TOKEN_EXPIRE")
        editor.apply()
    }
}