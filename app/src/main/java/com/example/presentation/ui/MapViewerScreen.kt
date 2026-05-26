package com.huhx0015.questadventure.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huhx0015.questadventure.domain.model.GameMetadata
import com.huhx0015.questadventure.domain.model.Point
import com.huhx0015.questadventure.domain.model.ScreenFeature
import com.huhx0015.questadventure.presentation.ui.components.GridOverlay
import com.huhx0015.questadventure.presentation.ui.components.RetroCRTFrame
import com.huhx0015.questadventure.presentation.ui.components.RetroScreenCanvas
import com.huhx0015.questadventure.presentation.viewmodel.Direction
import com.huhx0015.questadventure.presentation.viewmodel.MapUiState
import com.huhx0015.questadventure.presentation.viewmodel.MapViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun getGameAbbreviation(id: String): String {
    return when (id.lowercase()) {
        "kq1" -> "Quest I"
        "kq2" -> "Quest II"
        "kq3" -> "Quest III"
        "kq4" -> "Quest IV"
        "kq5" -> "Quest V"
        "kq6" -> "Quest VI"
        "kq7" -> "Quest VII"
        else -> id.uppercase()
    }
}

@Composable
fun QuestAdventureHeader(selectedGameTitle: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Quest Adventure",
                color = Color(0xFFE6E1E5),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
            Text(
                text = selectedGameTitle.uppercase(),
                color = Color(0xFFD0BCFF),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun GridCoordinateStatusBar(
    currentX: Int,
    currentY: Int,
    currentRoomTitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF313033), shape = RoundedCornerShape(18.dp))
                .border(1.dp, Color(0xFF49454F), shape = RoundedCornerShape(18.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "SCREEN: $currentX-$currentY",
                color = Color(0xFFD0BCFF),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "CURRENT LOCATION",
                color = Color(0xFFCAC4D0),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
            Text(
                text = currentRoomTitle,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun MapViewerScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Simulated terminal history logs
    var terminalHistory by remember { mutableStateOf(listOf<String>()) }
    // Initialize history on start
    LaunchedEffect(state.selectedGame) {
        state.selectedGame?.let {
            terminalHistory = listOf(
                "SYSTEM IDLE... SEED READY.",
                "BOOTING METADATA: ${it.title} (${it.releaseYear})",
                "STORY: ${it.story}",
                "INITIAL COORDINATE LOADED AT [${it.initialX}, ${it.initialY}]",
                "SWIPE SCREEN OR CLICK ARROWS TO TRAVEL."
            )
        }
    }

    // Monitor coordinate transitions to inject simulated text events
    LaunchedEffect(state.currentPoint) {
        state.currentScreen?.let {
            terminalHistory = (terminalHistory + listOf(
                "> LOOK COORDINATE [${state.currentPoint.x}, ${state.currentPoint.y}]",
                "ROOM: ${it.title}",
                "DESC: ${it.description}"
            )).takeLast(25) // Keep last 25 items for scroll space
        }
    }

    // Monitor error messages (Sierra blocks)
    LaunchedEffect(state.blockMessage) {
        state.blockMessage?.let {
            terminalHistory = (terminalHistory + listOf(
                "> MOVE UNABLE",
                "SIERRA WARNING: $it"
            )).takeLast(25)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1C1B1F), // Dark obsidian backing
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD0BCFF))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Top Custom Header (Matches modern HTML QuestAdventure UI)
                QuestAdventureHeader(selectedGameTitle = state.selectedGame?.title ?: "No Selected Game")

                if (isTablet) {
                    // Wide Screen Multi-Pane Grid Layout
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Game Carousel & CRT Map Screen (60% width)
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GridCoordinateStatusBar(
                                currentX = state.currentPoint.x,
                                currentY = state.currentPoint.y,
                                currentRoomTitle = state.currentScreen?.title ?: "WILDERNESS MAP"
                            )

                            CRTMapScreen(
                                state = state,
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f)
                            )

                            GameSelectionCarousel(
                                games = state.games,
                                selectedGame = state.selectedGame,
                                onGameChange = { viewModel.selectGame(it) }
                            )
                        }

                        // Right Column: Radar Minimap & Parser log panel (40% width)
                        Column(
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.selectedGame?.let { game ->
                                GridOverlay(
                                    width = game.width,
                                    height = game.height,
                                    currentPoint = state.currentPoint,
                                    visitedPoints = state.visitedPoints,
                                    themeColor = Color(android.graphics.Color.parseColor(game.themeColorHex)),
                                    landmarks = getLandmarkCoordinatesForGame(game.id),
                                    onCellClick = { /* Click to warp optional, or view */ }
                                )
                            }

                            TerminalParserPanel(
                                state = state,
                                history = terminalHistory,
                                onCommandClick = { cmd ->
                                    val (log, actionMsg) = triggerSimulatedCommand(cmd, state)
                                    terminalHistory = (terminalHistory + log).takeLast(25)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    // Phone Screen Compact Vertical Scrolling Layout
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GridCoordinateStatusBar(
                            currentX = state.currentPoint.x,
                            currentY = state.currentPoint.y,
                            currentRoomTitle = state.currentScreen?.title ?: "WILDERNESS MAP"
                        )

                        CRTMapScreen(
                            state = state,
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        )

                        GameSelectionCarousel(
                            games = state.games,
                            selectedGame = state.selectedGame,
                            onGameChange = { viewModel.selectGame(it) }
                        )

                        state.selectedGame?.let { game ->
                            GridOverlay(
                                width = game.width,
                                height = game.height,
                                currentPoint = state.currentPoint,
                                visitedPoints = state.visitedPoints,
                                themeColor = Color(android.graphics.Color.parseColor(game.themeColorHex)),
                                landmarks = getLandmarkCoordinatesForGame(game.id),
                                onCellClick = { /* Minimap interact */ }
                            )
                        }

                        TerminalParserPanel(
                            state = state,
                            history = terminalHistory,
                            onCommandClick = { cmd ->
                                val (log, actionMsg) = triggerSimulatedCommand(cmd, state)
                                terminalHistory = (terminalHistory + log).takeLast(25)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameSelectionCarousel(
    games: List<GameMetadata>,
    selectedGame: GameMetadata?,
    onGameChange: (GameMetadata) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("game_carousel"),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(games) { game ->
                val isSelected = game.id == selectedGame?.id
                val boxBgColor = if (isSelected) Color(0xFFD0BCFF) else Color(0xFF49454F)
                val textCol = if (isSelected) Color(0xFF381E72) else Color(0xFFCAC4D0)
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(boxBgColor)
                        .clickable { onGameChange(game) }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .testTag("carousel_item_${game.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getGameAbbreviation(game.id),
                        color = textCol,
                        fontSize = 13.sp,
                        fontWeight = fontWeight,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

@Composable
fun NavCircleAction(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun CRTMapScreen(
    state: MapUiState,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val themeColor = Color(android.graphics.Color.parseColor(state.selectedGame?.themeColorHex ?: "#0000AA"))

    // Swipe Detector Logic that behaves like ViewPager2D
    var dragTriggered by remember(state.currentPoint) { mutableStateOf(false) }

    RetroCRTFrame(
        modifier = modifier.testTag("crt_monitor"),
        gameTitle = state.selectedGame?.title ?: "UNKNOWN"
    ) {
        // Display animated transitions of current rooms
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.currentPoint) {
                    detectDragGestures(
                        onDragStart = { dragTriggered = false },
                        onDragEnd = { dragTriggered = false },
                        onDragCancel = { dragTriggered = false },
                        onDrag = { change, dragAmount ->
                            if (!dragTriggered) {
                                val dx = dragAmount.x
                                val dy = dragAmount.y
                                val threshold = 55f // swipe sensitivity pixels
                                if (Math.abs(dx) > Math.abs(dy)) {
                                    if (Math.abs(dx) > threshold) {
                                        dragTriggered = true
                                        if (dx > 0) {
                                            viewModel.move(Direction.WEST) // Swipe Right -> reveal left
                                        } else {
                                            viewModel.move(Direction.EAST) // Swipe Left -> reveal right
                                        }
                                        viewModel.clearBlockMessage()
                                    }
                                } else {
                                    if (Math.abs(dy) > threshold) {
                                        dragTriggered = true
                                        if (dy > 0) {
                                            viewModel.move(Direction.NORTH) // Swipe Down -> reveal top
                                        } else {
                                            viewModel.move(Direction.SOUTH) // Swipe Up -> reveal bottom
                                        }
                                        viewModel.clearBlockMessage()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            // Procedural landscape layer
            AnimatedContent(
                targetState = state.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "ScreenTransition"
            ) { screen ->
                if (screen != null) {
                    RetroScreenCanvas(
                        feature = screen.feature,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EMPTY SPACE - OUT OF REALM",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // HUD Overlay showing room title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color(0xE60D0C0B))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = state.currentScreen?.title?.uppercase() ?: "WILDERNESS MAP",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    state.currentScreen?.landmarkName?.let {
                        Text(
                            text = "POI: $it",
                            color = themeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Virtual Game Controller Overlay: Precise Circle Buttons
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // North Arrow
                NavCircleAction(
                    onClick = { viewModel.move(Direction.NORTH); viewModel.clearBlockMessage() },
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Go North",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 44.dp)
                        .testTag("nav_north")
                )

                // South Arrow
                NavCircleAction(
                    onClick = { viewModel.move(Direction.SOUTH); viewModel.clearBlockMessage() },
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Go South",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .testTag("nav_south")
                )

                // West Arrow
                NavCircleAction(
                    onClick = { viewModel.move(Direction.WEST); viewModel.clearBlockMessage() },
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go West",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("nav_west")
                )

                // East Arrow
                NavCircleAction(
                    onClick = { viewModel.move(Direction.EAST); viewModel.clearBlockMessage() },
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go East",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("nav_east")
                )
            }

            // Pulse danger alert in corner
            if (state.currentScreen?.dangerLevel != "Safe") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(
                            if (state.currentScreen?.dangerLevel == "Deadly") Color(0xCCEF4444) else Color(0xCCF59E0B),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "HAZARD: ${state.currentScreen?.dangerLevel?.uppercase()}",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Discovery overlay showing percentage explored
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.65f), shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                val totalRooms = (state.selectedGame?.width ?: 1) * (state.selectedGame?.height ?: 1)
                val exploredPct = (state.visitedPoints.size.toFloat() / totalRooms * 100).toInt()
                Text(
                    text = "DISCOVERED: $exploredPct%",
                    color = Color(0xFF10B981),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun TerminalParserPanel(
    state: MapUiState,
    history: List<String>,
    onCommandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Keep scrolls pinned to bottom to mimic old CLI terminal typing
    LaunchedEffect(history.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .background(Color(0xFF313033), shape = RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF49454F), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Output logs display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (log in history) {
                    val color = when {
                        log.startsWith(">") -> Color(0xFFD0BCFF) // Lavendar typed commands
                        log.startsWith("SIERRA WARNING:") -> Color(0xFFEF4444) // Error Red
                        log.startsWith("ROOM:") -> Color(0xFF38BDF8) // High cyan Info
                        else -> Color(0xFF10B981) // Classical green phosphate terminal
                    }
                    Text(
                        text = log,
                        color = color,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Divider(
            color = Color(0xFF49454F),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Simulated action command panel
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "EXECUTE SIMULATED COMMANDS:",
                color = Color(0xFFCAC4D0),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("LOOK", "TAKE", "TALK", "HINT").forEach { cmd ->
                    Button(
                        onClick = { onCommandClick(cmd) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF49454F),
                            contentColor = Color(0xFFE6E1E5)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .testTag("action_$cmd")
                    ) {
                        Text(
                            text = cmd,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

object RowDefaults {
    val borderStroke = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4B5563))
}

// Handler for simulated retro text-adventure command strings
private fun triggerSimulatedCommand(cmd: String, state: MapUiState): Pair<List<String>, String> {
    val screen = state.currentScreen
    val logList = mutableListOf<String>()

    logList.add("> $cmd")

    return when (cmd) {
        "LOOK" -> {
            if (screen == null) {
                Pair(logList + "You look around but see nothing but an empty dark void.", "Look failed")
            } else {
                var extra = ""
                if (screen.items.isNotEmpty()) {
                    extra += " You spot items here: ${screen.items.joinToString()}."
                }
                if (screen.characters.isNotEmpty()) {
                    extra += " You notice characters here: ${screen.characters.joinToString()}."
                }
                Pair(logList + "You investigate your surroundings carefully. ${screen.description}$extra", "Looking...")
            }
        }
        "TAKE" -> {
            if (screen == null || screen.items.isEmpty()) {
                Pair(logList + "There are no notable items on this coordinate screen to inventory.", "Carry nothing")
            } else {
                Pair(logList + "You reach out and secure the following: ${screen.items.joinToString()}. Placed nicely in Sir Graham's adventurers pouch!", "Taking items")
            }
        }
        "TALK" -> {
            if (screen == null || screen.characters.isEmpty()) {
                Pair(logList + "Only the rustle of leaves answers you. There is no one in this clearing.", "Talking to trees")
            } else {
                val replies = when (screen.feature) {
                    ScreenFeature.CASTLE -> "The royal guards snap to attention! 'Hold there adventurer! Secure the lost treasures of Daventry to speak with His highness!'"
                    ScreenFeature.TOWN -> "The shopkeepers and townspeople greet Alexander: 'Greetings traveler! Beware of the wizard's spies on Llewdor paths!'"
                    else -> "The characters glance at your royal crest and nod sagely, sharing secret details of paths."
                }
                Pair(logList + "You speak with: ${screen.characters.joinToString()}. $replies", "Talking...")
            }
        }
        "HINT" -> {
            val hintText = screen?.hint ?: "This coordinate is a peaceful pathway. Swipe to discover landmarks or search for items."
            Pair(logList + "SIERRA HINT: $hintText", "Hinting...")
        }
        else -> Pair(logList + "UNKNOWN CLI SYNTAX.", "Unknown")
    }
}

private fun getLandmarkCoordinatesForGame(gameId: String): List<Point> {
    return when (gameId) {
        "kq1" -> listOf(Point(7,2), Point(7,3), Point(4,0), Point(2,3), Point(0,0), Point(4,4), Point(1,1), Point(2,0))
        "kq2" -> listOf(Point(0,0), Point(2,4), Point(7,0), Point(3,2))
        "kq3" -> listOf(Point(3,0), Point(3,3), Point(5,2))
        "kq4" -> listOf(Point(0,0), Point(4,2), Point(7,4))
        "kq5" -> listOf(Point(3,3), Point(0,2), Point(2,5))
        "kq6" -> listOf(Point(2,2), Point(0,1), Point(4,1))
        "kq7" -> listOf(Point(2,2), Point(4,4))
        else -> emptyList()
    }
}
