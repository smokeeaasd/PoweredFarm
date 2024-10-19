package dev.lucas.poweredFarm.util

import dev.lucas.poweredFarm.config.Configuration

class CropUtil {
    companion object {
        fun getCropDisplayName(cropType: String): String {
            return Configuration.messages.crops.first { it.type.equals(cropType, true) }.displayName
        }
    }
}