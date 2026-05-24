package com.example.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.domain.model.ScreenFeature

@Composable
fun RetroScreenCanvas(
    feature: ScreenFeature,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Draw Sky/Horizon Background
        drawSky(feature, w, h)

        // 2. Draw Midground / Landscape main forms
        drawMidground(feature, w, h)

        // 3. Draw Foreground thematic details
        drawForegroundDetails(feature, w, h)
    }
}

private fun DrawScope.drawSky(feature: ScreenFeature, w: Float, h: Float) {
    when (feature) {
        ScreenFeature.CEMETERY -> {
            // Dark Spooky Purple to Deep Blue Horizon
            drawRect(
                color = Color(0xFF1E0B36),
                size = Size(w, h * 0.5f)
            )
            // Orange spooky moon
            drawCircle(
                color = Color(0xFFFFB83D),
                radius = h * 0.1f,
                center = Offset(w * 0.8f, h * 0.2f)
            )
        }
        ScreenFeature.DESERT -> {
            // Bright Scorching Cyan/Yellow EGA Sky
            drawRect(
                color = Color(0xFF22D3EE),
                size = Size(w, h * 0.45f)
            )
            // Hot white sun with light yellow aura
            drawCircle(
                color = Color(0xFFFFFFE0),
                radius = h * 0.12f,
                center = Offset(w * 0.2f, h * 0.18f)
            )
            drawCircle(
                color = Color.White,
                radius = h * 0.08f,
                center = Offset(w * 0.2f, h * 0.18f)
            )
        }
        ScreenFeature.SPECIAL -> {
            // Starry Cosmic Black Space
            drawRect(
                color = Color(0xFF0F0F1A),
                size = Size(w, h)
            )
            // Draw little stars
            val starPositions = listOf(
                Offset(w * 0.1f, h * 0.1f), Offset(w * 0.3f, h * 0.25f),
                Offset(w * 0.15f, h * 0.4f), Offset(w * 0.55f, h * 0.08f),
                Offset(w * 0.75f, h * 0.35f), Offset(w * 0.9f, h * 0.12f)
            )
            for (p in starPositions) {
                drawCircle(color = Color.White, radius = 3f, center = p)
                drawCircle(color = Color(0xFFFFFF55), radius = 1.5f, center = p)
            }
        }
        ScreenFeature.SWAMP -> {
            // Pale Green Fog Sky
            drawRect(
                color = Color(0xFF2E3D30),
                size = Size(w, h * 0.45f)
            )
        }
        else -> {
            // Classical EGA Sky Blue
            drawRect(
                color = Color(0xFF55FFFF),
                size = Size(w, h * 0.5f)
            )
            // Yellow EGA Sun
            drawCircle(
                color = Color(0xFFFFFF55),
                radius = h * 0.09f,
                center = Offset(w * 0.85f, h * 0.18f)
            )
        }
    }
}

private fun DrawScope.drawMidground(feature: ScreenFeature, w: Float, h: Float) {
    val groundY = h * 0.45f

    when (feature) {
        ScreenFeature.MOUNTAIN -> {
            // Jagged mountain peaks (Grey and White glaciers)
            val path = Path().apply {
                moveTo(0f, groundY)
                lineTo(w * 0.25f, h * 0.08f) // peak 1
                lineTo(w * 0.45f, h * 0.35f)
                lineTo(w * 0.7f, h * 0.05f)  // peak 2 (tall)
                lineTo(w * 0.9f, h * 0.28f)
                lineTo(w, groundY)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path, color = Color(0xFF555555)) // Grey rock

            // Draw Mountain snowy tops
            val snow1 = Path().apply {
                moveTo(w * 0.18f, h * 0.15f)
                lineTo(w * 0.25f, h * 0.08f)
                lineTo(w * 0.32f, h * 0.15f)
                lineTo(w * 0.25f, h * 0.19f)
                close()
            }
            drawPath(snow1, color = Color.White)

            val snow2 = Path().apply {
                moveTo(w * 0.63f, h * 0.12f)
                lineTo(w * 0.7f, h * 0.05f)
                lineTo(w * 0.77f, h * 0.12f)
                lineTo(w * 0.7f, h * 0.16f)
                close()
            }
            drawPath(snow2, color = Color.White)

            // Brown foreground dirt trail
            drawRect(
                color = Color(0xFFAA5500),
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )
        }
        ScreenFeature.DESERT -> {
            // Golden EGA Desert Dunes
            drawRect(
                color = Color(0xAAFF55),
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )

            // Curved Sand Dunes using paths
            val dune1 = Path().apply {
                moveTo(0f, groundY + 40f)
                cubicTo(w * 0.3f, groundY - 20f, w * 0.7f, groundY + 60f, w, groundY)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(dune1, color = Color(0xFFFFAA00))

            val dune2 = Path().apply {
                moveTo(0f, h * 0.75f)
                cubicTo(w * 0.4f, h * 0.6f, w * 0.8f, h * 0.85f, w, h * 0.68f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(dune2, color = Color(0xFFD97706))
        }
        ScreenFeature.SWAMP -> {
            // Murky green/grey mud
            drawRect(
                color = Color(0xFF1E2E20),
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )

            // Spooky marsh pools (dark toxic cyan)
            drawOval(
                color = Color(0xFF0D5E4E),
                topLeft = Offset(w * 0.1f, h * 0.6f),
                size = Size(w * 0.5f, h * 0.18f)
            )
            drawOval(
                color = Color(0xFF0D5E4E),
                topLeft = Offset(w * 0.55f, h * 0.75f),
                size = Size(w * 0.35f, h * 0.12f)
            )
        }
        ScreenFeature.WATER -> {
            // Sandy Beach to Deep Blue sea
            drawRect(
                color = Color(0xFFFCD34D), // Beach gold
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )

            val seaPath = Path().apply {
                moveTo(0f, groundY + 20f)
                cubicTo(w * 0.2f, groundY - 10f, w * 0.5f, groundY + 50f, w * 0.8f, groundY + 10f)
                lineTo(w, groundY + 5f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(seaPath, color = Color(0xFF0055AA)) // Deep Blue water

            // Waves strokes
            for (i in 1..4) {
                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * (0.2f * i), h * (0.55f + i * 0.08f)),
                    size = Size(40f, 15f)
                )
            }
        }
        ScreenFeature.CEMETERY -> {
            // Dark creepy grass
            drawRect(
                color = Color(0xFF0B1F13),
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )
        }
        else -> {
            // standard green meadow background
            drawRect(
                color = Color(0xFF00AA00), // EGA grass green
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )
        }
    }
}

private fun DrawScope.drawForegroundDetails(feature: ScreenFeature, w: Float, h: Float) {
    val cy = h * 0.55f

    when (feature) {
        ScreenFeature.CASTLE -> {
            // Draw a stout gray medieval castle base
            val castleLeft = w * 0.25f
            val castleTop = h * 0.35f
            val castleW = w * 0.5f
            val castleH = h * 0.35f

            // Castle main body
            drawRect(
                color = Color(0xFF888888),
                topLeft = Offset(castleLeft, castleTop),
                size = Size(castleW, castleH)
            )

            // Draw turrets/battlements
            val turretW = castleW / 7f
            for (i in 0..6 step 2) {
                drawRect(
                    color = Color(0xFF555555),
                    topLeft = Offset(castleLeft + (i * turretW), castleTop - 30f),
                    size = Size(turretW, 30f)
                )
            }

            // Draw a sturdy wooden door
            drawRect(
                color = Color(0xFFAA5500),
                topLeft = Offset(w * 0.43f, h * 0.52f),
                size = Size(w * 0.14f, h * 0.18f)
            )
            // Arch top for door
            drawCircle(
                color = Color(0xFFAA5500),
                radius = w * 0.07f,
                center = Offset(w * 0.5f, h * 0.52f)
            )

            // Draw a blue moat river running in foreground
            drawRect(
                color = Color(0xFF00AAAA),
                topLeft = Offset(0f, h * 0.72f),
                size = Size(w, h * 0.18f)
            )
            // Wooden bridge across the moat
            drawRect(
                color = Color(0xFFFF5555), // Red wood bridge
                topLeft = Offset(w * 0.4f, h * 0.7f),
                size = Size(w * 0.2f, h * 0.12f)
            )
        }
        ScreenFeature.TOWN -> {
            // Draw simple overlapping cute point-and-click retro cottages/shops
            val colors = listOf(Color(0xFFFFAA00), Color(0xFF55FF55), Color(0xFFFF55FF))
            for (i in 0..2) {
                val dx = w * (0.15f + i * 0.25f)
                val dy = h * (0.38f + i * 0.04f)
                val sw = w * 0.22f
                val sh = h * 0.25f

                // House Wall
                drawRect(
                    color = colors[i],
                    topLeft = Offset(dx, dy),
                    size = Size(sw, sh)
                )

                // Pointed roof Path
                val roof = Path().apply {
                    moveTo(dx - 10f, dy)
                    lineTo(dx + sw / 2f, dy - 35f)
                    lineTo(dx + sw + 10f, dy)
                    close()
                }
                drawPath(roof, color = Color(0xFFAA0000))

                // Door and window
                drawRect(
                    color = Color(0xFF555555),
                    topLeft = Offset(dx + sw * 0.15f, dy + sh * 0.4f),
                    size = Size(sw * 0.25f, sh * 0.5f)
                )
                // Window
                drawRect(
                    color = Color(0xFFFFFF55),
                    topLeft = Offset(dx + sw * 0.55f, dy + sh * 0.25f),
                    size = Size(20f, 20f)
                )
            }
        }
        ScreenFeature.FOREST -> {
            // Draw beautiful retro pixel-looking fir trees and broadleaf trees
            drawDeciduousTree(w * 0.18f, h * 0.42f, h * 0.32f, Color(0xFF00AA00), Color(0xFF55FF55))
            drawConiferTree(w * 0.5f, h * 0.38f, h * 0.35f)
            drawDeciduousTree(w * 0.8f, h * 0.45f, h * 0.3f, Color(0xFF005500), Color(0xFF00AA00))
        }
        ScreenFeature.MOUNTAIN -> {
            // Draw high rock formations on side edges
            drawRect(
                color = Color(0xFF333333),
                topLeft = Offset(0f, h * 0.45f),
                size = Size(w * 0.12f, h * 0.55f)
            )
            drawRect(
                color = Color(0xFF333333),
                topLeft = Offset(w * 0.88f, h * 0.45f),
                size = Size(w * 0.12f, h * 0.55f)
            )
            // Winding brown dirt step line
            val steps = Path().apply {
                moveTo(w * 0.2f, h)
                lineTo(w * 0.5f, h * 0.75f)
                lineTo(w * 0.4f, h * 0.65f)
                lineTo(w * 0.7f, h * 0.52f)
                lineTo(w * 0.65f, h * 0.45f)
            }
            drawPath(
                steps,
                color = Color(0xFFAA5500),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f)
            )
        }
        ScreenFeature.DESERT -> {
            // Draw a tall green cactus and cute little rocks
            val cx = w * 0.75f
            val baseCY = h * 0.55f
            // Main trunks
            drawRect(
                color = Color(0xFF00AA00),
                topLeft = Offset(cx, baseCY),
                size = Size(20f, h * 0.3f)
            )
            // Left arm
            drawRect(
                color = Color(0xFF00AA00),
                topLeft = Offset(cx - 30f, baseCY + 30f),
                size = Size(30f, 15f)
            )
            drawRect(
                color = Color(0xFF00AA00),
                topLeft = Offset(cx - 30f, baseCY + 10f),
                size = Size(15f, 30f)
            )
            // Right arm
            drawRect(
                color = Color(0xFF00AA00),
                topLeft = Offset(cx + 20f, baseCY + 50f),
                size = Size(35f, 15f)
            )
            drawRect(
                color = Color(0xFF00AA00),
                topLeft = Offset(cx + 40f, baseCY + 25f),
                size = Size(15f, 40f)
            )

            // Draw a sparkling blue water hole far left (Oasis if overlay has it, or generally)
            drawOval(
                color = Color(0xFF55FFFF),
                topLeft = Offset(w * 0.15f, h * 0.7f),
                size = Size(w * 0.3f, h * 0.15f)
            )
        }
        ScreenFeature.HOUSE -> {
            // Cozy cabin
            val hx = w * 0.32f
            val hy = h * 0.42f
            val hw = w * 0.36f
            val hh = h * 0.28f

            // Log walls (Brown stripes)
            drawRect(
                color = Color(0xFFAA5500),
                topLeft = Offset(hx, hy),
                size = Size(hw, hh)
            )
            for (i in 1..4) {
                drawLine(
                    color = Color(0xFF552200),
                    start = Offset(hx, hy + i * (hh / 5f)),
                    end = Offset(hx + hw, hy + i * (hh / 5f)),
                    strokeWidth = 4f
                )
            }

            // High triangle roof
            val roof = Path().apply {
                moveTo(hx - 15f, hy)
                lineTo(hx + hw / 2f, hy - 45f)
                lineTo(hx + hw + 15f, hy)
                close()
            }
            drawPath(roof, color = Color(0xFFAA0000))

            // Door
            drawRect(
                color = Color(0xFF552200),
                topLeft = Offset(hx + hw * 0.38f, hy + hh * 0.4f),
                size = Size(hw * 0.24f, hh * 0.6f)
            )
            // Chimney with circles for puffs of smoke!
            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(hx + hw * 0.15f, hy - 35f),
                size = Size(15f, 35f)
            )
            drawCircle(color = Color(0xFFCCCCCC), radius = 10f, center = Offset(hx + hw * 0.15f + 7f, hy - 50f))
            drawCircle(color = Color(0xFFBBBBBB), radius = 15f, center = Offset(hx + hw * 0.15f + 12f, hy - 70f))
        }
        ScreenFeature.CEMETERY -> {
            // Gloomy cemetery with standard gray tombstones
            val gravesX = listOf(w * 0.15f, w * 0.42f, w * 0.72f)
            val gravesY = listOf(h * 0.58f, h * 0.64f, h * 0.52f)

            for (i in gravesX.indices) {
                val gx = gravesX[i]
                val gy = gravesY[i]
                // Tombstone arch
                drawRect(
                    color = Color.Gray,
                    topLeft = Offset(gx, gy),
                    size = Size(35f, 45f)
                )
                drawCircle(
                    color = Color.Gray,
                    radius = 17.5f,
                    center = Offset(gx + 17.5f, gy)
                )
                // Black RIP text representation lines
                drawLine(
                    color = Color.Black,
                    start = Offset(gx + 10f, gy + 10f),
                    end = Offset(gx + 25f, gy + 10f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(gx + 17.5f, gy + 5f),
                    end = Offset(gx + 17.5f, gy + 25f),
                    strokeWidth = 3f
                )
            }

            // A creepy gnarled dead tree
            val tx = w * 0.82f
            val ty = h * 0.45f
            drawLine(Color.DarkGray, Offset(tx, ty), Offset(tx, h), strokeWidth = 14f)
            drawLine(Color.DarkGray, Offset(tx, ty + 20f), Offset(tx - 35f, ty - 15f), strokeWidth = 8f)
            drawLine(Color.DarkGray, Offset(tx, ty + 40f), Offset(tx + 40f, ty + 10f), strokeWidth = 8f)
        }
        ScreenFeature.SWAMP -> {
            // Swamp leaves and hanging weeping mosses from edges
            val moss = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.15f, h * 0.35f)
                lineTo(w * 0.25f, 0f)
                lineTo(w * 0.6f, h * 0.28f)
                lineTo(w * 0.72f, 0f)
                lineTo(w, h * 0.4f)
                lineTo(w, 0f)
                close()
            }
            drawPath(moss, color = Color(0xFF1E261E))
        }
        ScreenFeature.PATH -> {
            // Crossroad brown paths with a double wooden signpost!
            drawRect(
                color = Color(0xFFAA5500),
                topLeft = Offset(w * 0.38f, h * 0.45f),
                size = Size(w * 0.24f, h * 0.55f)
            )
            drawRect(
                color = Color(0xFFAA5500),
                topLeft = Offset(0f, h * 0.62f),
                size = Size(w, h * 0.22f)
            )

            // Wooden Signpost
            val sx = w * 0.25f
            val sy = h * 0.48f
            // Vertical bar
            drawRect(
                color = Color(0xFF552200),
                topLeft = Offset(sx, sy),
                size = Size(10f, 65f)
            )
            // Left pointer sign
            val signL = Path().apply {
                moveTo(sx - 40f, sy + 10f)
                lineTo(sx + 5f, sy + 10f)
                lineTo(sx + 5f, sy + 25f)
                lineTo(sx - 40f, sy + 25f)
                lineTo(sx - 52f, sy + 17.5f)
                close()
            }
            drawPath(signL, color = Color(0xFFA57D56))
            // Right pointer sign
            val signR = Path().apply {
                moveTo(sx + 5f, sy + 28f)
                lineTo(sx + 50f, sy + 28f)
                lineTo(sx + 62f, sy + 35.5f)
                lineTo(sx + 50f, sy + 43f)
                lineTo(sx + 5f, sy + 43f)
                close()
            }
            drawPath(signR, color = Color(0xFFA57D56))
        }
        ScreenFeature.SPECIAL -> {
            // Celestial magical portal in center with glowing rays
            val px = w * 0.5f
            val py = h * 0.5f
            val radius = h * 0.22f

            // Swirling portal colors
            val pColors = listOf(Color(0xFFFF55FF), Color(0xFF55FFFF), Color(0xFFFFFF55))
            for (i in pColors.indices) {
                drawCircle(
                    color = pColors[i],
                    radius = radius - (i * 20f),
                    center = Offset(px, py)
                )
            }
            drawCircle(
                color = Color(0xFF0F0F1A),
                radius = radius - (pColors.size * 20f),
                center = Offset(px, py)
            )
        }
        else -> {
            // General decor for meadows: simple green blades
            for (i in 1..6) {
                val bx = w * (0.15f * i)
                val by = h * (0.55f + 0.05f * i)
                drawLine(
                    color = Color(0xFF55FF55),
                    start = Offset(bx, by),
                    end = Offset(bx - 10f, by - 25f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF55FF55),
                    start = Offset(bx, by),
                    end = Offset(bx + 12f, by - 20f),
                    strokeWidth = 3f
                )
            }
        }
    }
}

private fun DrawScope.drawDeciduousTree(
    x: Float,
    y: Float,
    height: Float,
    trunkColor: Color = Color(0xFF331E0C),
    leafColor: Color = Color(0xFF00AA00),
    leafHighlight: Color = Color(0xFF55FF55)
) {
    // Trunk
    val trunkW = height * 0.12f
    val trunkH = height * 0.4f
    drawRect(
        color = Color(0xFFAA5500),
        topLeft = Offset(x - trunkW / 2, y + height - trunkH),
        size = Size(trunkW, trunkH)
    )

    // Foliage layers (Overlapping circles)
    val r = height * 0.28f
    drawCircle(color = leafColor, radius = r, center = Offset(x, y + r))
    drawCircle(color = leafColor, radius = r * 0.8f, center = Offset(x - r * 0.5f, y + r * 1.1f))
    drawCircle(color = leafColor, radius = r * 0.8f, center = Offset(x + r * 0.5f, y + r * 1.1f))

    // High accent highlights
    drawCircle(color = leafHighlight, radius = r * 0.5f, center = Offset(x, y + r * 0.8f))
}

private fun DrawScope.drawConiferTree(x: Float, y: Float, height: Float) {
    // Trunk
    val trunkW = height * 0.09f
    val trunkH = height * 0.25f
    drawRect(
        color = Color(0xFF552200),
        topLeft = Offset(x - trunkW / 2, y + height - trunkH),
        size = Size(trunkW, trunkH)
    )

    // Triangular Pine sheets (3 layers)
    val colorPineDark = Color(0xFF005500)
    val colorPineLight = Color(0xFF00AA00)

    for (i in 0..2) {
        val dy = y + (i * (height * 0.22f))
        val sw = height * (0.45f - i * 0.1f)
        val sh = height * 0.35f

        val path = Path().apply {
            moveTo(x, dy)
            lineTo(x - sw, dy + sh)
            lineTo(x + sw, dy + sh)
            close()
        }
        drawPath(path, color = if (i == 0) colorPineLight else colorPineDark)
    }
}
