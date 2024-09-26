package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.database.DatabaseFactory
import dev.lucas.poweredFarm.database.models.Crop
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger) {

    private val configFile = File(dataFolder, "config.yml")

    companion object {
        var crops = mutableListOf<Crop>()
        var locale: String = "en_US"
        var cropMessages = mutableListOf<CropMessage>()
    }

    init {
        dataFolder.mkdirs() // Garantir que a pasta seja criada, se não existir
    }

    fun initialize() {
        createConfigFile()
        createMessagesDirectory()
        createMessageFiles()
        initializeDatabase()
        saveLocale()
        loadMessages()
    }

    private fun initializeDatabase() {
        DatabaseFactory.init()
        loadCrops()
        saveLocale()
    }

    private fun saveLocale() {
        locale = getConfig().getString("locale") ?: "en_US"
    }

    private fun loadCrops() {
        Crop.clear()
        crops = getConfig().getList("crops")?.mapNotNull { cropData ->
            (cropData as? Map<*, *>)?.let {
                val type = it["type"] as? String ?: return@mapNotNull null
                val limit = it["limit"] as? Int ?: 0
                Crop.create(type, limit)
            }
        }?.toMutableList() ?: mutableListOf()
    }

    private fun loadMessages() {
        cropMessages = getMessageConfig(locale).getList("crops")?.mapNotNull { cropMessageData ->
            (cropMessageData as? Map<*, *>)?.let {
                val type = it["type"] as? String ?: return@mapNotNull null
                val title = it["title"] as? String ?: return@mapNotNull null
                val loreLines = it["lore"] as? List<*> ?: return@mapNotNull null
                val fullText = it["full"] as? String ?: return@mapNotNull null

                CropMessage(
                    type,
                    parseText(title),
                    buildLore(loreLines),
                    parseText(fullText)
                )
            }
        }?.toMutableList() ?: mutableListOf()
    }

    private fun createConfigFile() {
        if (!configFile.exists()) {
            copyResource("config.yml", configFile)
            logger.info("Created config.yml")
        } else {
            logger.info("File 'config.yml' already exists: ${configFile.path}")
        }
    }

    private fun createMessagesDirectory() {
        val messagesDir = File(dataFolder, "messages")
        if (!messagesDir.exists()) {
            messagesDir.mkdirs()
            logger.info("Created 'messages' folder: ${messagesDir.path}")
        } else {
            logger.info("Folder 'messages' already exists: ${messagesDir.path}")
        }
    }

    private fun createMessageFiles() {
        listOf("pt_BR.yml", "en_US.yml").forEach { fileName ->
            val file = File(dataFolder, "messages/$fileName")
            if (!file.exists()) {
                copyResource("messages/$fileName", file)
                logger.info("Created '$fileName'.")
            } else {
                logger.info("File '$fileName' already exists: ${file.path}")
            }
        }
    }

    private fun copyResource(resourcePath: String, destination: File) {
        this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: logger.warning("Resource not found: $resourcePath")
    }

    private fun getConfig(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(configFile)
    }

    private fun getMessageConfig(locale: String): YamlConfiguration {
        val localeFile = File(dataFolder, "messages/${locale}.yml")
        return YamlConfiguration.loadConfiguration(localeFile)
    }

    private fun parseText(text: String): Component {
        return Component.text(text.replace("&", "§"))
    }

    private fun buildLore(loreLines: List<*>): Component {
        val lore = Component.text()
        loreLines.forEach { line ->
            lore.append(parseText(line as String))
        }
        return lore.build()
    }
}