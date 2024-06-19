package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.talkee.data.repository.HistoryRepository
import com.bangkit.talkee.data.response.ErrorResponse
import com.bangkit.talkee.data.response.HistoryDetailResponse
import com.bangkit.talkee.data.response.HistoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HistoryViewModel(private val repo: HistoryRepository) : ViewModel() {

    private val _historyResponse = MutableLiveData<HistoryResponse>()
    val historyResponse: LiveData<HistoryResponse> = _historyResponse

    private val _historyDetailResponse = MutableLiveData<HistoryDetailResponse>()
    val historyDetailResponse: LiveData<HistoryDetailResponse> = _historyDetailResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getAllHistory(token: String) {
        viewModelScope.launch {
            try {
                val result = repo.getAllHistory(token)
                _historyResponse.postValue(result)
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

    fun getHistoryDetail(token: String, idRiwayat: String) {
        viewModelScope.launch {
            try {
                val result = repo.getHistoryDetail(token, idRiwayat)
                _historyDetailResponse.postValue(result)
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