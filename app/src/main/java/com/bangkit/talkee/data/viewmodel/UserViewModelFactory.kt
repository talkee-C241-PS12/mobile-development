package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.repository.UserRepository

class UserViewModelFactory (private val authRepo: UserRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(authRepo) as T
            else -> throw IllegalArgumentException("Unidentified ViewModel: ${modelClass.name}")
        }
    }
}