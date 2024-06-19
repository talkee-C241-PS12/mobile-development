package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.response.ErrorResponse
import com.bangkit.talkee.data.response.ProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class UserViewModel(private val repo: UserRepository) : ViewModel() {

    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val profileResponse: LiveData<ProfileResponse> = _profileResponse

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val result = repo.getUserProfile(token)
                _profileResponse.postValue(result)
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

    fun signInUser(token: String, fileImage: File, name: String, email: String) {
        viewModelScope.launch {
            try {
                val result = repo.signInUser(token, fileImage, name, email).status ?: ""
                _successMessage.postValue(result)
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

    fun updateUser(token: String, fileImage: File, name: String) {
        viewModelScope.launch {
            try {
                val result = repo.updateUser(token, fileImage, name).status ?: ""
                _successMessage.postValue(result)
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