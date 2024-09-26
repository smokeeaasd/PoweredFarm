package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.database.DatabaseInitializer
import dev.lucas.poweredFarm.database.models.Crop
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
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

    fun initialize() {
        val resourceManager = ResourceManager(dataFolder, logger)
        resourceManager.createConfigFile(configFile)
        val validator = ConfigurationValidator(configFile, logger)
        if (!validator.validateConfig()) {
            logger.severe("Error on config.yml format.")
            logger.severe("Plugin will be disabled.")
            Bukkit.getPluginManager().disablePlugin(plugin)
            return
        }

        resourceManager.createMessagesDirectory()
        resourceManager.createMessageFiles()
        val databaseInitializer = DatabaseInitializer(this, logger)
        databaseInitializer.initializeDatabase()
        saveLocale()
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
}