package com.unibo.cyberopoli.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unibo.cyberopoli.data.repositories.game.GameRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            Log.d("TEST", "HomeViewModel init")
            gameRepository.preloadQuestionsForUser()
        }
    }
}
