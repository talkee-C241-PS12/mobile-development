package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.KelasDetailResponse
import com.bangkit.talkee.data.response.KelasResponse
import com.bangkit.talkee.data.retrofit.ApiService

class KelasRepository(private val apiService: ApiService) {

    suspend fun getAllKelas(): KelasResponse {
        return try {
            val response = apiService.getAllKelas()
            response
        } catch (e: Exception) {
            KelasResponse()
        }
    }

    suspend fun getKelasDetail(idKelas: String): KelasDetailResponse {
        return try {
            val response = apiService.getKelasDetail(idKelas)
            response
        } catch (e: Exception) {
            KelasDetailResponse()
        }
    }
}