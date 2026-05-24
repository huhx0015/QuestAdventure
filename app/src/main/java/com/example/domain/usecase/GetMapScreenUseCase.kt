package com.example.domain.usecase

import com.example.domain.model.MapScreen
import com.example.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetMapScreenUseCase(private val repository: GameRepository) {
    operator fun invoke(gameId: String, x: Int, y: Int): Flow<MapScreen?> =
        repository.getMapScreen(gameId, x, y)
}
