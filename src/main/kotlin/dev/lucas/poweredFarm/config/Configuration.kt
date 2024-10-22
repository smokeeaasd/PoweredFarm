package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.validators.ConfigValidator
import dev.lucas.poweredFarm.database.DatabaseInitializer
import dev.lucas.poweredFarm.database.models.Crop
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger, private val plugin: Main) {
    private val configFile = File(dataFolder, "config.yml")

    companion object {
        var crops = mutableListOf<Crop>()
        var locale: String = "en_US"
        lateinit var messages: Messages
    }

    init {
        dataFolder.mkdirs()
    }

    fun initialize(): Boolean {
        saveLocale()
        val configValidator = ConfigValidator(dataFolder, logger, configFile)
        if (!configValidator.validateConfig()) {
            plugin.safeDisable()
            return false
        }
        if (!configValidator.validateLocale()) {
            plugin.safeDisable()
            return false
        }

        loadMessages()
        val databaseInitializer = DatabaseInitializer(this)
        databaseInitializer.initializeDatabase()
        return true
    }

    private fun loadMessages() {
        val messageLoader = MessageLoader(this)
        messageLoader.loadMessages()
    }

    private fun saveLocale() {
        locale = getConfig().getString("locale") ?: "en_US"
        Bukkit.getConsoleSender().sendMessage(locale)
    }

    fun getConfig(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun getMessageConfig(locale: String): YamlConfiguration {
        val localeFile = File(dataFolder, "locales/${locale}.yml")
        return YamlConfiguration.loadConfiguration(localeFile)
    }

    fun parseText(text: String): String {
        return text.replace("&", "§")
    }
}