package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.talkee.data.repository.LeaderboardRepository
import com.bangkit.talkee.data.response.ErrorResponse
import com.bangkit.talkee.data.response.LeaderboardResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LeaderboardViewModel(private val repo: LeaderboardRepository) : ViewModel() {

    private val _leaderboardResponse = MutableLiveData<LeaderboardResponse>()
    val leaderboardResponse: LiveData<LeaderboardResponse> = _leaderboardResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getLeaderboard() {
        viewModelScope.launch {
            try {
                val result = repo.getLeaderboard()
                _leaderboardResponse.postValue(result)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.error ?: ""
                _errorMessage.postValue(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                _errorMessage.postValue(errorMessage)
            }
        }
    }

    fun clearSuccessMessage() { _successMessage.value = "" }

    fun clearErrorMessage() { _errorMessage.value = "" }
}