package com.huhx0015.questadventure.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huhx0015.questadventure.domain.usecase.GetGamesUseCase
import com.huhx0015.questadventure.domain.usecase.GetMapScreenUseCase

class MapViewModelFactory(
    private val getGamesUseCase: GetGamesUseCase,
    private val getMapScreenUseCase: GetMapScreenUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(getGamesUseCase, getMapScreenUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
