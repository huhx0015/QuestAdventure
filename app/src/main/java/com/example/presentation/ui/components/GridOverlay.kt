package com.huhx0015.questadventure.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huhx0015.questadventure.domain.model.Point

@Composable
fun GridOverlay(
    width: Int,
    height: Int,
    currentPoint: Point,
    visitedPoints: Set<Point>,
    themeColor: Color,
    landmarks: List<Point> = emptyList(),
    onCellClick: (Point) -> Unit
) {
    // Pulse animation for current location
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF313033), shape = RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF49454F), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("radar_minimap")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EGA GRID RADAR MAP (${width}x${height})",
                color = Color(0xFFE6E1E5),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "COORDINATE: [${currentPoint.x}, ${currentPoint.y}]",
                color = Color(0xFFD0BCFF),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Draw rows
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (row in 0 until height) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (col in 0 until width) {
                        val cellPoint = Point(col, row)
                        val isCurrent = cellPoint == currentPoint
                        val isVisited = visitedPoints.contains(cellPoint)
                        val isLandmark = landmarks.contains(cellPoint)

                        // Visual styling
                        val cellBg = when {
                            isCurrent -> Color(0xFFFBBF24) // Gold current location
                            isLandmark -> Color(0xFFD0BCFF) // Special Landmark (Geometric Balance Lavender)
                            isVisited -> Color(0xFF10B981) // Visited green
                            else -> Color(0xFF100F12) // Unexplored deep geometric dark
                        }

                        val modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .background(cellBg, shape = RoundedCornerShape(3.dp))
                            .alpha(if (isCurrent) pulseAlpha else 1f)
                            .border(
                                1.dp,
                                if (isCurrent) Color.White else Color(0x33FFFFFF),
                                RoundedCornerShape(3.dp)
                            )
                            .clickable { onCellClick(cellPoint) }

                        Box(
                            modifier = modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent) {
                                Text(
                                    text = "G",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else if (isLandmark) {
                                Text(
                                    text = "★",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Legends
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(backgroundColor = Color(0xFFFBBF24), label = "GRAHAM")
            LegendItem(backgroundColor = Color(0xFF10B981), label = "VISITED")
            LegendItem(backgroundColor = Color(0xFFD0BCFF), label = "LANDMARK")
            LegendItem(backgroundColor = Color(0xFF100F12), label = "DARK")
        }
    }
}

@Composable
fun LegendItem(backgroundColor: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(1.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
