package dev.lucas.poweredFarm.config.validators

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

class ConfigurationValidator(private val configFile: File, private val logger: Logger) {

    fun validateConfig(): Boolean {
        val validCropTypes = setOf(
            "nether_wart", "beetroot", "carrot", "potato", "wheat"
        )
        val validLocales = setOf("pt_BR", "en_US")

        val config = YamlConfiguration()

        try {
            config.load(configFile)
        } catch (e: Exception) {
            logger.warning("Failed to load configuration file: ${e.message}")
            return false
        }

        val locale = config.getString("locale")
        if (locale == null || !validLocales.contains(locale)) {
            logger.warning("Invalid locale in configuration file. Must be one of: $validLocales")
            return false
        }

        val crops = config.getList("crops")
        if (crops == null || crops.isEmpty()) {
            logger.warning("The crops list is empty or missing in the configuration file.")
            return false
        }

        crops.forEach { cropData ->
            val crop = cropData as? Map<*, *> ?: return@forEach
            val type = crop["type"] as? String
            val limit = crop["limit"] as? Int

            if (type == null || !validCropTypes.contains(type)) {
                logger.warning("Invalid crop type: $type. Must be one of: $validCropTypes")
                return false
            }

            if (limit == null || limit <= 0) {
                logger.warning("Invalid crop limit. The limit must be a positive number.")
                return false
            }
        }

        logger.info("Configuration file validated successfully!")
        return true
    }
}