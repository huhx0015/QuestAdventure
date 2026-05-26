package com.huhx0015.questadventure.domain.usecase

import com.huhx0015.questadventure.domain.model.MapScreen
import com.huhx0015.questadventure.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetMapScreenUseCase(private val repository: GameRepository) {
    operator fun invoke(gameId: String, x: Int, y: Int): Flow<MapScreen?> =
        repository.getMapScreen(gameId, x, y)
}
