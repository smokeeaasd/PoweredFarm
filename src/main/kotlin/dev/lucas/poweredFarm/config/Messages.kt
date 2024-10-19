package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.config.messages.CropMessage
import dev.lucas.poweredFarm.config.messages.StorageMessage

data class Messages(
    val crops: MutableList<CropMessage>,
    val storage: StorageMessage,
    val commandMessages: Map<String, String>
)