package dev.lucas.poweredFarm.config.validators

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
            val displayName = crop["display-name"] as? String
            val title = crop["title"] as? String
            val lore = crop["lore"] as? List<*>

            if (type.isNullOrEmpty()) {
                logger.warning("Crop with invalid type: $type.")
                return false
            }

            if (displayName.isNullOrEmpty()) {
                logger.warning("Crop with invalid display name for type $type.")
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

        val iconSection = storage.getConfigurationSection("icon")
        if (iconSection == null) {
            logger.warning("The icon section is missing in the storage configuration.")
            return false
        }

        val iconTitle = iconSection.getString("title")
        if (iconTitle.isNullOrEmpty()) {
            logger.warning("The icon title is missing or empty in the storage configuration.")
            return false
        }

        val iconLore = iconSection.getStringList("lore")
        if (iconLore.isEmpty()) {
            logger.warning("The icon lore is missing or empty in the storage configuration.")
            return false
        }

        val collectSection = storage.getConfigurationSection("collect")
        if (collectSection == null) {
            logger.warning("The collect section is missing in the storage configuration.")
            return false
        }

        val collectTitle = collectSection.getString("title")
        if (collectTitle.isNullOrEmpty()) {
            logger.warning("The collect title is missing or empty in the storage configuration.")
            return false
        }

        val collectLore = collectSection.getStringList("lore")
        if (collectLore.isEmpty()) {
            logger.warning("The collect lore is missing or empty in the storage configuration.")
            return false
        }

        logger.info("Locale file validated successfully!")
        return true
    }
}