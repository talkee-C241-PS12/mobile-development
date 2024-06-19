package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.talkee.data.repository.GameRepository
import com.bangkit.talkee.data.response.ErrorResponse
import com.bangkit.talkee.data.response.GameAnswerResponse
import com.bangkit.talkee.data.response.GameDetailResponse
import com.bangkit.talkee.data.response.GameResponse
import com.bangkit.talkee.data.response.GameStartResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GameViewModel(private val repo: GameRepository) : ViewModel() {

    private val _gameResponse = MutableLiveData<GameResponse>()
    val gameResponse: LiveData<GameResponse> = _gameResponse

    private val _gameDetailResponse = MutableLiveData<GameDetailResponse>()
    val gameDetailResponse: LiveData<GameDetailResponse> = _gameDetailResponse

    private val _gameStartResponse = MutableLiveData<GameStartResponse>()
    val gameStartResponse: LiveData<GameStartResponse> = _gameStartResponse

    private val _gameAnswerResponse = MutableLiveData<GameAnswerResponse>()
    val gameAnswerResponse: LiveData<GameAnswerResponse> = _gameAnswerResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getAllGames() {
        viewModelScope.launch {
            try {
                val result = repo.getAllGames()
                _gameResponse.postValue(result)
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

    fun getGameDetail(idGame: String) {
        viewModelScope.launch {
            try {
                val result = repo.getGameDetail(idGame)
                _gameDetailResponse.postValue(result)
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

    fun startGame(token: String, idGame: String) {
        viewModelScope.launch {
            try {
                val result = repo.startGame(token, idGame)
                _gameStartResponse.postValue(result)
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

    fun answerQuestion(token: String, idpertanyaan: String, jawaban: String) {
        viewModelScope.launch {
            try {
                val result = repo.answerQuestion(token, idpertanyaan, jawaban)
                _gameAnswerResponse.postValue(result)
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