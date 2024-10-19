package dev.lucas.poweredFarm.config.messages

data class CropMessage(
    val type: String,
    val displayName: String,
    val title: String,
    val lore: List<String>
)