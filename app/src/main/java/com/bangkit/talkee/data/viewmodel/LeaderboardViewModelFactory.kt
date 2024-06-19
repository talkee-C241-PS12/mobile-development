package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.repository.LeaderboardRepository

class LeaderboardViewModelFactory (private val repo: LeaderboardRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) -> LeaderboardViewModel(repo) as T
            else -> throw IllegalArgumentException("Unidentified ViewModel: ${modelClass.name}")
        }
    }
}