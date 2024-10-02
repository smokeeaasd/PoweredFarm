package dev.lucas.poweredFarm.config

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
            logger.warning("Locale inválido no arquivo de configuração. Deve ser um dos seguintes: $validLocales")
            return false
        }

        val crops = config.getList("crops")
        if (crops == null || crops.isEmpty()) {
            logger.warning("A lista de crops está vazia ou ausente no arquivo de configuração.")
            return false
        }

        crops.forEach { cropData ->
            val crop = cropData as? Map<*, *> ?: return@forEach
            val type = crop["type"] as? String
            val limit = crop["limit"] as? Int

            if (type == null || !validCropTypes.contains(type)) {
                logger.warning("Crop com tipo inválido: $type. Deve ser um dos seguintes: $validCropTypes")
                return false
            }

            if (limit == null || limit <= 0) {
                logger.warning("Crop com limit inválido. O limite deve ser um número positivo.")
                return false
            }
        }

        logger.info("Arquivo de configuração validado com sucesso!")
        return true
    }
}