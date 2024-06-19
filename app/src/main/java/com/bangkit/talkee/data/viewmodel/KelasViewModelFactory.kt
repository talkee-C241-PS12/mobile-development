package com.bangkit.talkee.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.repository.KelasRepository

class KelasViewModelFactory (private val repo: KelasRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(KelasViewModel::class.java) -> KelasViewModel(repo) as T
            else -> throw IllegalArgumentException("Unidentified ViewModel: ${modelClass.name}")
        }
    }
}