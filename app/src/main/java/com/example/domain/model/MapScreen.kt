package com.example.domain.model

enum class ScreenFeature {
    CASTLE,       // High-status royal buildings
    TOWN,         // Towns, shops, villages
    FOREST,       // Standard wooded realms
    MOUNTAIN,     // Steep trails, vertical cliffs
    DESERT,       // Barren dunes, oases
    WATER,        // Moats, ocean beaches, rivers, waterfalls
    HOUSE,        // Singular cottage, treehouse, cave, or home
    CEMETERY,     // Graveyards or spooky ruins
    SWAMP,        // Foggy bogs and marshy trails
    PATH,         // Crossroads or simple meadows
    SPECIAL       // Cloudland, underworld, or alternate dimensions
}

data class MapScreen(
    val x: Int,
    val y: Int,
    val title: String,
    val description: String,
    val feature: ScreenFeature,
    val landmarkName: String? = null,
    val items: List<String> = emptyList(),
    val characters: List<String> = emptyList(),
    val dangerLevel: String = "Safe", // "Safe", "Mild", "Deadly"
    val hint: String? = null
)
