package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.repository.HistoryRepository

class HistoryViewModelFactory (private val repo: HistoryRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repo) as T
            else -> throw IllegalArgumentException("Unidentified ViewModel: ${modelClass.name}")
        }
    }
}