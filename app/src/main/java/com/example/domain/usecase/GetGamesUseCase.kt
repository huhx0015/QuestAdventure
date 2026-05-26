package com.huhx0015.questadventure.domain.usecase

import com.huhx0015.questadventure.domain.model.GameMetadata
import com.huhx0015.questadventure.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<GameMetadata>> = repository.getGames()
}
