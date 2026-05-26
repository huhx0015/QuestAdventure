package com.huhx0015.questadventure.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huhx0015.questadventure.domain.model.BoundaryType
import com.huhx0015.questadventure.domain.model.GameMetadata
import com.huhx0015.questadventure.domain.model.MapScreen
import com.huhx0015.questadventure.domain.model.Point
import com.huhx0015.questadventure.domain.usecase.GetGamesUseCase
import com.huhx0015.questadventure.domain.usecase.GetMapScreenUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

data class MapUiState(
    val games: List<GameMetadata> = emptyList(),
    val selectedGame: GameMetadata? = null,
    val currentPoint: Point = Point(0, 0),
    val currentScreen: MapScreen? = null,
    val visitedPoints: Set<Point> = emptySet(),
    val blockMessage: String? = null,
    val isLoading: Boolean = false
)

class MapViewModel(
    private val getGamesUseCase: GetGamesUseCase,
    private val getMapScreenUseCase: GetMapScreenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getGamesUseCase().collect { gamesList ->
                _uiState.update { state ->
                    val defaultGame = gamesList.firstOrNull()
                    state.copy(
                        games = gamesList,
                        selectedGame = defaultGame,
                        currentPoint = defaultGame?.let { Point(it.initialX, it.initialY) } ?: Point(0, 0),
                        visitedPoints = defaultGame?.let { setOf(Point(it.initialX, it.initialY)) } ?: emptySet(),
                        isLoading = false
                    )
                }
                val initialGame = gamesList.firstOrNull()
                if (initialGame != null) {
                    fetchScreen(initialGame.id, initialGame.initialX, initialGame.initialY)
                }
            }
        }
    }

    fun selectGame(game: GameMetadata) {
        _uiState.update { state ->
            val startingPoint = Point(game.initialX, game.initialY)
            state.copy(
                selectedGame = game,
                currentPoint = startingPoint,
                visitedPoints = setOf(startingPoint),
                blockMessage = null
            )
        }
        fetchScreen(game.id, game.initialX, game.initialY)
    }

    fun clearBlockMessage() {
        _uiState.update { it.copy(blockMessage = null) }
    }

    fun move(direction: Direction) {
        val state = _uiState.value
        val game = state.selectedGame ?: return
        val current = state.currentPoint

        var targetX = current.x
        var targetY = current.y

        when (direction) {
            Direction.NORTH -> targetY -= 1
            Direction.SOUTH -> targetY += 1
            Direction.EAST -> targetX += 1
            Direction.WEST -> targetX -= 1
        }

        if (game.boundaryType == BoundaryType.WRAP) {
            targetX = (targetX % game.width + game.width) % game.width
            targetY = (targetY % game.height + game.height) % game.height
            val newPoint = Point(targetX, targetY)
            _uiState.update { it.copy(
                currentPoint = newPoint,
                visitedPoints = it.visitedPoints + newPoint,
                blockMessage = null
            )}
            fetchScreen(game.id, targetX, targetY)
        } else {
            if (targetX in 0 until game.width && targetY in 0 until game.height) {
                val newPoint = Point(targetX, targetY)
                _uiState.update { it.copy(
                    currentPoint = newPoint,
                    visitedPoints = it.visitedPoints + newPoint,
                    blockMessage = null
                )}
                fetchScreen(game.id, targetX, targetY)
            } else {
                val message = getSierraBlockMessage(game.id, direction)
                _uiState.update { it.copy(blockMessage = message) }
            }
        }
    }

    private fun fetchScreen(gameId: String, x: Int, y: Int) {
        viewModelScope.launch {
            getMapScreenUseCase(gameId, x, y).collect { screen ->
                _uiState.update { it.copy(currentScreen = screen) }
            }
        }
    }

    private fun getSierraBlockMessage(gameId: String, direction: Direction): String {
        return when (gameId) {
            "kq4" -> {
                when (direction) {
                    Direction.NORTH -> "The massive mountains rise steeply here, blocking Rosella's path further north."
                    Direction.SOUTH -> "Dense ocean tides and treacherous undertows block Rosella's path into the deep seas."
                    Direction.WEST -> "The swamp is too deep and quicksand-laden to cross safely this way."
                    Direction.EAST -> "A cliff drops off into dark ocean water. You cannot go that way!"
                }
            }
            "kq5" -> {
                when (direction) {
                    Direction.NORTH -> "Steep snowy cliffs block Graham's ascent. The ice looks slippery."
                    Direction.SOUTH -> "The desert heat is too blazing, and there is no water source to navigate further south."
                    Direction.WEST -> "Endless, hot, burning sand dunes extend forever. Wandering here without direction is fatal."
                    Direction.EAST -> "Dangerous dark forest thickets stand dense and intertwined, preventing passage."
                }
            }
            "kq6" -> {
                when (direction) {
                    Direction.NORTH -> "The jagged cliffs are too vertical to climb without tools."
                    Direction.SOUTH -> "Tides of the Green Isles sea rise, dangerous coral reefs preventing passage."
                    Direction.WEST -> "Rough waves crash on Alexander's ship wreckage. You cannot wade into the ocean!"
                    Direction.EAST -> "Strange ancient stone runes and thick magical barriers block Alexander's path."
                }
            }
            "kq7" -> {
                when (direction) {
                    Direction.NORTH -> "Whispering sky clouds are too thin. You cannot fly higher on foot."
                    Direction.SOUTH -> "The eerie swamp shadows grow dense. Rosella cannot penetrate deeper in this direction."
                    Direction.WEST -> "A glowing lava fissure turns the air hot. You can go no further west."
                    Direction.EAST -> "Steep canyon walls separate the land. Going east here means dropping off a cliff!"
                }
            }
            else -> "You cannot go that way. A physical obstacle stands in your path, keeping you within explorer coordinates."
        }
    }
}
