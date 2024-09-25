package dev.lucas.poweredFarm.database.dto.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

data class CropMessageDTO(
    val type: String,
    val title: TextComponent,
    val lore: TextComponent,
    val full: Component
)