package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.ProfileResponse
import com.bangkit.talkee.data.response.ProfileUpdateResponse
import com.bangkit.talkee.data.response.SignInResponse
import com.bangkit.talkee.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository(private val apiService: ApiService) {

    suspend fun getUserProfile(token: String): ProfileResponse {
        return try {
            val response = apiService.getProfile(token)
            response
        } catch (e: Exception) {
            ProfileResponse()
        }
    }

    suspend fun signInUser(token: String, fileImage: File, name: String, email: String): SignInResponse {
        val userName = name.toRequestBody("text/plain".toMediaType())
        val userEmail = email.toRequestBody("text/plain".toMediaType())
        val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", fileImage.name, requestImageFile)
        return apiService.signInUser(token, imageMultipart, userName, userEmail)
    }

    suspend fun updateUser(token: String, fileImage: File, name: String): ProfileUpdateResponse {
        val userName = name.toRequestBody("text/plain".toMediaType())
        val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", fileImage.name, requestImageFile)
        return apiService.updateProfile(token, userName, imageMultipart)
    }
}