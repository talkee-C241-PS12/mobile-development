package com.bangkit.talkee.data.retrofit

import com.bangkit.talkee.data.response.GameAnswerResponse
import com.bangkit.talkee.data.response.GameDetailResponse
import com.bangkit.talkee.data.response.GameResponse
import com.bangkit.talkee.data.response.GameStartResponse
import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.data.response.HistoryResponse
import com.bangkit.talkee.data.response.KelasDetailResponse
import com.bangkit.talkee.data.response.KelasResponse
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.bangkit.talkee.data.response.ProfileResponse
import com.bangkit.talkee.data.response.ProfileUpdateResponse
import com.bangkit.talkee.data.response.SignInResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("api/register")
    suspend fun signInUser(
        @Header("token") token: String,
        @Part image: MultipartBody.Part,
        @Part("nama") name: RequestBody,
        @Part("email") email: RequestBody
    ): SignInResponse

    @GET("api/profile")
    suspend fun getProfile(
        @Query("token") token: String
    ): ProfileResponse

    @Multipart
    @POST("api/profile")
    suspend fun updateProfile(
        @Header("token") token: String,
        @Part("nama") name: RequestBody,
        @Part image: MultipartBody.Part,
    ): ProfileUpdateResponse

    @GET("api/kelas")
    suspend fun getAllKelas() : KelasResponse

    @GET("api/kelas/detail")
    suspend fun getKelasDetail(
        @Query("idkelas") idKelas: String
    ) : KelasDetailResponse

    @GET("api/game")
    suspend fun getAllGames() : GameResponse

    @Multipart
    @POST("api/game/start")
    suspend fun startGame(
        @Header("token") token: String,
        @Part("idgame") idGame: RequestBody,
    ) : GameStartResponse

    @GET("api/game/detail")
    suspend fun getGameDetail(
        @Query("idgame") idGame: String
    ) : GameDetailResponse

    @Multipart
    @POST("api/game/answer")
    suspend fun answerQuestion(
        @Header("token") token: String,
        @Part("idpertanyaan") idPertanyaan: RequestBody,
        @Part("jawaban") jawaban: RequestBody,
    ) : GameAnswerResponse

    @GET("api/riwayat/game")
    suspend fun getAllHistory(
        @Query("token") token: String
    ) : HistoryResponse

    @GET("api/riwayat/game/detail")
    suspend fun getHistoryDetail(
        @Query("token") token: String,
        @Query("idriwayat") idRiwayat: String
    ) : HistoryDetailResponse

    @GET("api/leaderboard")
    suspend fun getLeaderboard() : LeaderboardResponse

    @Multipart
    @POST("api/predict")
    suspend fun predictModel() : LeaderboardResponse
}