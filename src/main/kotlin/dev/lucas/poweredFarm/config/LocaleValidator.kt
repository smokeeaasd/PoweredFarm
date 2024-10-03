package dev.lucas.poweredFarm.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

class LocaleValidator(private val localeFile: File, private val logger: Logger) {

    fun validateLocale(): Boolean {
        val config = YamlConfiguration()

        try {
            config.load(localeFile)
        } catch (e: Exception) {
            logger.warning("Failed to load locale file: ${e.message}")
            return false
        }

        val requiredKeys = listOf("crops", "storage")
        for (key in requiredKeys) {
            if (!config.contains(key)) {
                logger.warning("Locale file is missing required key: $key")
                return false
            }
        }

        val crops = config.getList("crops")
        if (crops == null || crops.isEmpty()) {
            logger.warning("The crops list is empty or missing in the locale file.")
            return false
        }

        crops.forEach { cropData ->
            val crop = cropData as? Map<*, *> ?: return@forEach
            val type = crop["type"] as? String
            val title = crop["title"] as? String
            val lore = crop["lore"] as? List<*>

            if (type.isNullOrEmpty()) {
                logger.warning("Crop with invalid type: $type.")
                return false
            }

            if (title.isNullOrEmpty()) {
                logger.warning("Crop with invalid title for type $type.")
                return false
            }

            if (lore == null || lore.isEmpty()) {
                logger.warning("Crop with invalid lore for type $type.")
                return false
            }
        }

        val storage = config.getConfigurationSection("storage")
        if (storage == null) {
            logger.warning("The storage section is missing in the locale file.")
            return false
        }

        val storageTitle = storage.getString("title")
        if (storageTitle.isNullOrEmpty()) {
            logger.warning("Storage title is missing or empty.")
            return false
        }

        logger.info("Locale file validated successfully!")
        return true
    }
}