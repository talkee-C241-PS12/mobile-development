package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.repository.GameRepository

class GameViewModelFactory (private val repo: GameRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(repo) as T
            else -> throw IllegalArgumentException("Unidentified ViewModel: ${modelClass.name}")
        }
    }
}