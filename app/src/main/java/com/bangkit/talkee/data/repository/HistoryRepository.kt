package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.data.retrofit.ApiService

class HistoryRepository(private val apiService: ApiService) {

    suspend fun getAllHistory(token: String): HistoryResponse {
        return try {
            val response = apiService.getAllHistory(token)
            response
        } catch (e: Exception) {
            HistoryResponse()
        }
    }

    suspend fun getHistoryDetail(token: String, idRiwayat: String): HistoryDetailResponse {
        return try {
            val response = apiService.getHistoryDetail(token, idRiwayat)
            response
        } catch (e: Exception) {
            HistoryDetailResponse()
        }
    }
}