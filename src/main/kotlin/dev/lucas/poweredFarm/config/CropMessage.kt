package dev.lucas.poweredFarm.config

data class CropMessage(
    val type: String,
    val title: String,
    val lore: List<String>,
    val full: String
)