package dev.lucas.poweredFarm.config.validators

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.config.ResourceManager
import java.io.File
import java.util.logging.Logger

class ConfigValidator(private val dataFolder: File, private val logger: Logger, private val configFile: File) {
    fun validateConfig(): Boolean {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createConfigFile(configFile)
        val validator = ConfigurationValidator(configFile, logger)

        if (!validator.validateConfig()) {
            logger.severe("Error on config.yml format.")
            return false
        }
        return true
    }

    fun validateLocale(): Boolean {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createMessagesDirectory()
        resourceManager.createMessageFiles()
        val localeFile = File(dataFolder, "locales/${Configuration.locale}.yml")
        val localeValidator = LocaleValidator(localeFile, logger)

        if (!localeValidator.validateLocale()) {
            logger.severe("Error on locale file format.")
            return false
        }
        return true
    }
}