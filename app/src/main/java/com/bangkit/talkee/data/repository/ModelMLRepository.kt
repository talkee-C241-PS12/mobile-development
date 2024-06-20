package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.ModelMLResponse
import com.bangkit.talkee.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ModelMLRepository(private val apiService: ApiService) {

    suspend fun uploadVideoAnswer(videoFile: File): ModelMLResponse {
        return try {
            val videoFilePart = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
            val videoMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("video", videoFile.name, videoFilePart)
            val response = apiService.predictModel(videoMultipart)
            response
        } catch (e: Exception) {
            ModelMLResponse()
        }
    }
}