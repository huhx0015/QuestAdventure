package com.example.domain.model

enum class BoundaryType {
    WRAP, // Torus wrap-around (KQ1, KQ2, KQ3)
    BLOCK // Blocked with classic Sierra boundaries (KQ4, KQ5, KQ6, KQ7)
}

data class GameMetadata(
    val id: String,
    val title: String,
    val subtitle: String,
    val releaseYear: String,
    val story: String,
    val themeColorHex: String,
    val accentColorHex: String,
    val boundaryType: BoundaryType,
    val width: Int,  // Grid columns (e.g., 8 col for KQ1)
    val height: Int, // Grid rows (e.g., 6 row for KQ1)
    val initialX: Int,
    val initialY: Int
)
