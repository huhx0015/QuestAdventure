package com.huhx0015.questadventure.domain.repository

import com.huhx0015.questadventure.domain.model.GameMetadata
import com.huhx0015.questadventure.domain.model.MapScreen
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGames(): Flow<List<GameMetadata>>
    fun getGame(id: String): Flow<GameMetadata?>
    fun getMapScreens(gameId: String): Flow<List<MapScreen>>
    fun getMapScreen(gameId: String, x: Int, y: Int): Flow<MapScreen?>
}
