package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.database.dto.CropDTO
import dev.lucas.poweredFarm.database.DatabaseFactory
import dev.lucas.poweredFarm.database.dto.messages.CropMessageDTO
import dev.lucas.poweredFarm.database.models.Crop
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger
import kotlin.reflect.typeOf

class Configuration(private val dataFolder: File, private val logger: Logger) {

    private val configFile = File(dataFolder, "config.yml")

    companion object {
        var crops = mutableListOf<CropDTO>()
        var locale: String = "en_US"
        var cropMessages = mutableListOf<CropMessageDTO>()
    }

    init {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
    }

    fun initialize() {
        createConfigFile()
        createMessagesDirectory()
        createMessageFiles()
        initializeDatabase()
        saveLocale()
        saveMessages()
    }

    private fun saveMessages() {
        val cropMessagesSection = getMessageConfig(locale).getList("crops") ?: return

        cropMessages = cropMessagesSection.mapNotNull { cropMessageData ->
            if (cropMessageData is Map<*, *>) {
                logger.info(cropMessageData["title"]!!::class.toString())
                logger.info(cropMessageData["type"]!!::class.toString())
                logger.info(cropMessageData["lore"]!!::class.toString())
                logger.info(cropMessageData["full"]!!::class.toString())
                val type = cropMessageData["type"] as? String ?: return@mapNotNull null
                val title = cropMessageData["title"] as? String ?: return@mapNotNull null
                val loreLines = cropMessageData["lore"] as? List<*> ?: return@mapNotNull null
                val full = cropMessageData["full"] as? String ?: return@mapNotNull null

                val lore = Component.text()
                for (line in loreLines) {
                    lore.append(Component.text(line as String))
                }
                CropMessageDTO(type, Component.text(title), lore.build(), Component.text(full))
            } else null
        }.toMutableList()
    }

    private fun initializeDatabase() {
        DatabaseFactory.init()
        saveCrops()
        saveLocale()
    }

    private fun saveLocale() {
        locale = getConfig().getString("locale") ?: "en_US"
    }

    private fun saveCrops() {
        Crop.clear()
        val cropsSection = getConfig().getList("crops") ?: return

        crops = cropsSection.mapNotNull { cropData ->
            if (cropData is Map<*, *>) {
                val type = cropData["type"] as? String ?: return@mapNotNull null
                val label = cropData["label"] as? String ?: return@mapNotNull null
                val limit = cropData["limit"] as? Int ?: 0

                Crop.create(type)
                CropDTO(type, label, limit)
            } else null
        }.toMutableList()
    }

    private fun createConfigFile() {
        if (configFile.exists()) {
            logger.info("File 'config.yml' already exists: ${configFile.path}")
            return
        }

        copyResource("config.yml", configFile)
        logger.info("Created config.yml")
    }

    private fun createMessagesDirectory() {
        val messagesDir = File(dataFolder, "messages")
        if (messagesDir.exists()) {
            logger.info("Folder 'messages' already exists: ${messagesDir.path}")
            return
        }

        messagesDir.mkdirs()
        logger.info("Created 'messages' folder: ${messagesDir.path}")
    }

    private fun createMessageFiles() {
        val messageFiles = listOf("pt_BR.yml", "en_US.yml")

        messageFiles.forEach { fileName ->
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
        val messagesDir = File(dataFolder, "messages")
        val localeFile = File(messagesDir, locale.plus(".yml"))
        return YamlConfiguration.loadConfiguration(localeFile)
    }
}
