package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.talkee.data.repository.KelasRepository
import com.bangkit.talkee.data.response.ErrorResponse
import com.bangkit.talkee.data.response.KelasDetailResponse
import com.bangkit.talkee.data.response.KelasResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class KelasViewModel(private val repo: KelasRepository) : ViewModel() {

    private val _kelasResponse = MutableLiveData<KelasResponse>()
    val kelasResponse: LiveData<KelasResponse> = _kelasResponse

    private val _kelasDetailResponse = MutableLiveData<KelasDetailResponse>()
    val kelasDetailResponse: LiveData<KelasDetailResponse> = _kelasDetailResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getAllKelas() {
        viewModelScope.launch {
            try {
                val result = repo.getAllKelas()
                _kelasResponse.postValue(result)
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

    fun getAllKelasDetail(idKelas: String) {
        viewModelScope.launch {
            try {
                val result = repo.getKelasDetail(idKelas)
                _kelasDetailResponse.postValue(result)
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