package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.config.dto.CropDTO
import dev.lucas.poweredFarm.database.DatabaseFactory
import dev.lucas.poweredFarm.database.models.Crop
import dev.lucas.poweredFarm.database.tables.Crops
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger) {
    companion object {
        var cropList = mutableListOf<CropDTO>()
        var locale: String = "en_US";
    }
    init {
        if (!dataFolder.exists())
            dataFolder.mkdirs()
    }

    fun initialize() {
        createConfigFile()
        createMessagesDirectory()
        createMessageFiles()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        DatabaseFactory.init()
        saveCrops()
        saveLocale()
    }

    private fun saveLocale() {
        locale = getConfig().getString("locale") ?: "en_US";
    }

    private fun saveCrops() {
        val cropsSection = getConfig().getMapList("crops")

        cropsSection.forEach { cropData ->
            if (cropData is Map<*, *>) {
                val type = cropData["type"] as? String ?: return@forEach
                val label = cropData["label"] as? String ?: return@forEach
                val enabled = cropData["enabled"] as? Boolean ?: true
                val limit = cropData["limit"] as? Int ?: 0
                cropList.add(CropDTO(type, label, enabled, limit))
                Crop.create(type)
            }
        }
    }

    private fun createConfigFile() {
        val configFile = File(dataFolder, "config.yml")
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

        for (fileName in messageFiles) {
            val file = File(dataFolder, "messages/$fileName")
            if (file.exists()) {
                logger.info("File '$fileName' already exists: ${file.path}")
                continue
            }

            copyResource("messages/$fileName", file)
            logger.info("Created '$fileName'.")
        }
    }

    private fun copyResource(resourcePath: String, destination: File) {
        this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: logger.warning("Resource not found: $resourcePath")
    }

    fun getConfig(): YamlConfiguration {
        val configFile = File(dataFolder, "config.yml")
        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun getMessageConfig(locale: String): YamlConfiguration {
        val messagesDir = File(dataFolder, "messages")
        val messageConfigFile = File(messagesDir, locale.plus(".yml"))
        return YamlConfiguration.loadConfiguration(messageConfigFile)
    }
}