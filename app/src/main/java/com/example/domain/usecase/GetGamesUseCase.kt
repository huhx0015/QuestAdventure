package com.example.domain.usecase

import com.example.domain.model.GameMetadata
import com.example.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GetGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<GameMetadata>> = repository.getGames()
}
