package com.bangkit.talkee.data.repository

import com.bangkit.talkee.data.response.GameAnswerResponse
import com.bangkit.talkee.data.response.GameDetailResponse
import com.bangkit.talkee.data.response.GameResponse
import com.bangkit.talkee.data.response.GameStartResponse
import com.bangkit.talkee.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GameRepository(private val apiService: ApiService) {

    suspend fun getAllGames(): GameResponse {
        return try {
            val response = apiService.getAllGames()
            response
        } catch (e: Exception) {
            GameResponse()
        }
    }

    suspend fun getGameDetail(idGame: String): GameDetailResponse {
        return try {
            val response = apiService.getGameDetail(idGame)
            response
        } catch (e: Exception) {
            GameDetailResponse()
        }
    }

    suspend fun startGame(token: String, idGame: String): GameStartResponse {
        return try {
            val idGamePart = idGame.toRequestBody("text/plain".toMediaType())
            val response = apiService.startGame(token, idGamePart)
            response
        } catch (e: Exception) {
            GameStartResponse(pertanyaan = emptyList())
        }
    }

    suspend fun answerQuestion(token: String, idpertanyaan: String, jawaban: String): GameAnswerResponse {
        return try {
            val idPertanyaanPart = idpertanyaan.toRequestBody("text/plain".toMediaType())
            val jawabanPart = jawaban.toRequestBody("text/plain".toMediaType())
            val response = apiService.answerQuestion(token, idPertanyaanPart, jawabanPart)
            response
        } catch (e: Exception) {
            GameAnswerResponse()
        }
    }
}