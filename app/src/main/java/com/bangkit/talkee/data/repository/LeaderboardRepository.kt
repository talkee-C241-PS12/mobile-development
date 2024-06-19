package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.bangkit.talkee.data.retrofit.ApiService

class LeaderboardRepository(private val apiService: ApiService) {

    suspend fun getLeaderboard(): LeaderboardResponse {
        return try {
            val response = apiService.getLeaderboard()
            response
        } catch (e: Exception) {
            LeaderboardResponse()
        }
    }
}