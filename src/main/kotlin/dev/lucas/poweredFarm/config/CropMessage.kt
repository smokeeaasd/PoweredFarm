package dev.lucas.poweredFarm.config

import net.kyori.adventure.text.Component

data class CropMessage(
    val type: String,
    val title: Component,
    val lore: Component,
    val full: Component
)