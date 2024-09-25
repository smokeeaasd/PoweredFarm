package dev.lucas.poweredFarm.config.dto

data class CropDTO(
    val type: String,
    val label: String,
    val enabled: Boolean,
    val limit: Int
)
