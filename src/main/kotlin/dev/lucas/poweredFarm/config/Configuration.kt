package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.validators.ConfigurationValidator
import dev.lucas.poweredFarm.config.validators.LocaleValidator
import dev.lucas.poweredFarm.database.DatabaseInitializer
import dev.lucas.poweredFarm.database.models.Crop
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger, private val plugin: Main) {
    private val configFile = File(dataFolder, "config.yml")

    companion object {
        var crops = mutableListOf<Crop>()
        var locale: String = "en_US"
        lateinit var storageMessage: StorageMessage
        lateinit var cropMessages: MutableList<CropMessage>
    }

    init {
        dataFolder.mkdirs()
    }

    fun initialize(): Boolean {
        if (!validateConfig()) return false
        if (!validateLocale()) return false

        loadMessages()
        val databaseInitializer = DatabaseInitializer(this)
        databaseInitializer.initializeDatabase()
        return true
    }

    private fun validateConfig(): Boolean {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createConfigFile(configFile)
        val validator = ConfigurationValidator(configFile, logger)

        if (!validator.validateConfig()) {
            logger.severe("Error on config.yml format.")
            disablePluginSafely()
            return false
        }
        return true
    }

    private fun validateLocale(): Boolean {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createMessagesDirectory()
        resourceManager.createMessageFiles()
        saveLocale()
        val localeValidator = LocaleValidator(File(dataFolder, "messages/${locale}.yml"), logger)

        if (!localeValidator.validateLocale()) {
            logger.severe("Error on locale file format.")
            disablePluginSafely()
            return false
        }
        return true
    }

    private fun loadMessages() {
        val messageLoader = MessageLoader(this)
        messageLoader.loadMessages()
    }

    fun saveLocale() {
        locale = getConfig().getString("locale") ?: "en_US"
    }

    fun getConfig(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun getMessageConfig(locale: String): YamlConfiguration {
        val localeFile = File(dataFolder, "messages/${locale}.yml")
        return YamlConfiguration.loadConfiguration(localeFile)
    }

    fun parseText(text: String): String {
        return text.replace("&", "§")
    }

    private fun disablePluginSafely() {
        try {
            if (plugin.isEnabled) {
                logger.info("Disabling plugin: ${plugin.name}")
                plugin.server.pluginManager.disablePlugin(plugin)
                logger.info("Plugin disabled successfully.")
            }
        } catch (e: IllegalStateException) {
            logger.severe("Failed to disable plugin due to IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            logger.severe("Failed to disable plugin: ${e.message}")
        }
    }
}