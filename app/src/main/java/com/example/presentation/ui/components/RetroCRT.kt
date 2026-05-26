package com.huhx0015.questadventure.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RetroCRTFrame(
    modifier: Modifier = Modifier,
    gameTitle: String,
    systemStatus: String = "ONLINE",
    content: @Composable BoxScope.() -> Unit
) {
    // High-Fidelity Geometric Balance Chassis Bezel Design
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF313033), shape = RoundedCornerShape(32.dp)) // Mid-grey chassis background
            .border(4.dp, Color(0xFF49454F), RoundedCornerShape(32.dp))       // Precise outline border matching HTML
            .padding(14.dp)
    ) {
        // Monitor Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QUESTMAPS MONITOR NETWORK",
                color = Color(0xFFCAC4D0),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Little glowing LED
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFD0BCFF), shape = RoundedCornerShape(50)) // Lavender power light
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = systemStatus,
                    color = Color(0xFFD0BCFF),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Inner CRT Screen Tube Frame with rounded 24dp corners like the prompt suggests
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
                .border(3.dp, Color(0xFF1E1C1A), RoundedCornerShape(24.dp))
                .drawBehind {
                    // Glass curvature glare shadow
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x08FFFFFF), Color(0x3B000000)),
                            center = Offset(size.width * 0.5f, size.height * 0.3f),
                            radius = size.width * 0.8f
                        )
                    )
                }
        ) {
            // Content (The map canvas / Swipe container)
            content()

            // Scanlines Overlay
            CRTScanlineOverlay()
        }

        // Monitor Base / Brand Label Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EGA MODE: 16 COLORS",
                color = Color(0xFF10B981), // Neon green EGA indicator
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "CALIBRATION: ONLINE",
                color = Color(0xFFCAC4D0),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun CRTScanlineOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 6f // height distance between horizontal scanlines
        var y = 0f
        while (y < size.height) {
            // Draw subtle black transparency lines
            drawLine(
                color = Color(0x22000000),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2.5f
            )
            y += step
        }

        // Add subtle screen edge shadow vignette
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0x2D000000),
                0.08f to Color.Transparent,
                0.92f to Color.Transparent,
                1f to Color(0x3B000000)
            )
        )
    }
}
