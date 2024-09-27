package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.database.DatabaseInitializer
import dev.lucas.poweredFarm.database.models.Crop
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger, private val plugin: Main) {
    private val configFile = File(dataFolder, "config.yml")

    companion object {
        var crops = mutableListOf<Crop>()
        var locale: String = "en_US"
        var cropMessages = mutableListOf<CropMessage>()
    }

    init {
        dataFolder.mkdirs()
    }

    fun initialize(): Boolean {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createConfigFile(configFile)
        val validator = ConfigurationValidator(configFile, logger)

        if (!validator.validateConfig()) {
            logger.severe("Error on config.yml format.")
            logger.severe("Plugin will be disabled.")
            disablePluginSafely()
            return false
        }

        resourceManager.createMessagesDirectory()
        resourceManager.createMessageFiles()
        val databaseInitializer = DatabaseInitializer(this)
        databaseInitializer.initializeDatabase()
        saveLocale()
        val localeValidator = LocaleValidator(File(dataFolder, "messages/${locale}.yml"), logger)

        if (!localeValidator.validateLocale()) {
            logger.severe("Error on locale file format.")
            logger.severe("Plugin will be disabled.")
            disablePluginSafely()
            return false
        }

        val messageLoader = MessageLoader(this)
        messageLoader.loadMessages()

        return true
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

    fun parseText(text: String): Component {
        return Component.text(text.replace("&", "§"))
    }

    fun buildLore(loreLines: List<*>): Component {
        val lore = Component.text()
        loreLines.forEach { line ->
            lore.append(parseText(line as String))
        }
        return lore.build()
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