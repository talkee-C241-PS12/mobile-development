package com.bangkit.talkee.utils

import android.content.Context
import com.bangkit.talkee.BuildConfig
import com.bangkit.talkee.data.preference.TokenManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

fun exchangeAuthCodeForAccessToken(context: Context, serverAuthCode: String, onCompleteListener: OnCompleteListener) {
    val client = OkHttpClient()
    val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
    val requestBody = RequestBody.create(mediaType,
        "code=$serverAuthCode" +
                "&client_id=${BuildConfig.WEB_CLIENT_ID}" +
                "&client_secret=${BuildConfig.CLIENT_SECRET}" +
                "&redirect_uri=" +
                "&grant_type=authorization_code")

    val request = Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(requestBody)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onCompleteListener.onComplete(false, null, null, null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            val jsonResponse = responseBody?.let { JSONObject(it) }

            if (jsonResponse != null) {
                val accessToken = jsonResponse.getString("access_token")
                val idToken = jsonResponse.getString("id_token")
                val expiresIn = jsonResponse.getInt("expires_in")

                val currentTime = System.currentTimeMillis()
                val expireTokenTime = currentTime + (expiresIn * 1000)

                val tokenManager = TokenManager(context)
                tokenManager.saveTokens(accessToken, idToken, expireTokenTime)

                onCompleteListener.onComplete(true, accessToken, idToken, expireTokenTime)
            }
        }
    })
}

interface OnCompleteListener {
    fun onComplete(success: Boolean, accessToken: String?, idToken: String?, expireTokenTime: Long?)
}
